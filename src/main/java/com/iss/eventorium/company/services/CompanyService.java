package com.iss.eventorium.company.services;

import com.iss.eventorium.company.dtos.CompanyRequestDto;
import com.iss.eventorium.company.dtos.CompanyResponseDto;
import com.iss.eventorium.company.dtos.ProviderCompanyDto;
import com.iss.eventorium.company.dtos.UpdateRequestDto;
import com.iss.eventorium.company.mappers.CompanyMapper;
import com.iss.eventorium.company.models.Company;
import com.iss.eventorium.company.repositories.CompanyRepository;
import com.iss.eventorium.shared.dtos.ImageResponseDto;
import com.iss.eventorium.shared.dtos.RemoveImageRequestDto;
import com.iss.eventorium.shared.exceptions.ImageNotFoundException;
import com.iss.eventorium.shared.exceptions.ImageUploadException;
import com.iss.eventorium.shared.mappers.CityMapper;
import com.iss.eventorium.shared.models.ImagePath;
import com.iss.eventorium.shared.utils.ImageUpload;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.services.AccountActivationService;
import com.iss.eventorium.user.services.AuthService;
import com.iss.eventorium.user.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class CompanyService {

    private final CompanyRepository repository;
    private final AccountActivationService accountActivationService;
    private final UserService userService;
    private final AuthService authService;
    private final CompanyRepository companyRepository;

    @Value("${image-path}")
    private String imagePath;

    public CompanyResponseDto createCompany(CompanyRequestDto companyRequestDto) {
        Company company = CompanyMapper.fromRequest(companyRequestDto);
        User provider = userService.findById(companyRequestDto.getProviderId());
        company.setProvider(provider);
        accountActivationService.sendActivationEmail(provider);
        return CompanyMapper.toResponse(repository.save(company));
    }

    public void uploadImages(Long id, List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return;
        }

        Company company = getCompanyById(id);
        List<ImagePath> paths = processImages(id, images);

        if (!paths.isEmpty()) {
            company.getPhotos().addAll(paths);
            repository.save(company);
        }
    }

    private Company getCompanyById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Company with id %s not found", id))
        );
    }

    private List<ImagePath> processImages(Long companyId, List<MultipartFile> images) {
        List<ImagePath> paths = new ArrayList<>();
        String uploadDir = StringUtils.cleanPath(imagePath + "companies/" + companyId + "/");

        for (MultipartFile image : images) {
            try {
                ImagePath imagePath = saveImage(uploadDir, image);
                if (imagePath != null) {
                    paths.add(imagePath);
                }
            } catch (IOException e) {
                throw new ImageUploadException("Error while uploading images");
            }
        }
        return paths;
    }

    private ImagePath saveImage(String uploadDir, MultipartFile image) throws IOException {
        String name = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
        String fileName = Instant.now().toEpochMilli() + "_" + name;

        ImageUpload.saveImage(uploadDir, fileName, image);
        String contentType = ImageUpload.getImageContentType(uploadDir, fileName);

        return ImagePath.builder()
                .path(fileName)
                .contentType(contentType)
                .build();
    }

    public ProviderCompanyDto getCompany() {
        User currentUser = authService.getCurrentUser();
        Company company = repository.getCompanyByProviderId(currentUser.getId());
        return CompanyMapper.toProviderCompanyResponse(company);
    }

    public List<ImageResponseDto> getImages(Long id) {
        Company company = getCompanyById(id);
        List<ImageResponseDto> images = new ArrayList<>();
        for (ImagePath imagePath : company.getPhotos()) {
            byte[] image = getImage(id, imagePath);
            images.add(new ImageResponseDto(imagePath.getId(), image, imagePath.getContentType()));
        }
        return images;
    }

    public byte[] getImage(Long id, ImagePath path) {
        String uploadDir = StringUtils.cleanPath(imagePath + "companies/" + id + "/");
        File file = new File(uploadDir, path.getPath());

        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new ImageNotFoundException("Fail to read image" + path.getPath() + ": + e.getMessage()");
        }
    }

    public CompanyResponseDto updateCompany(UpdateRequestDto updateRequestDto) {
        Company company = getCompanyById(updateRequestDto.getId());
        company.setAddress(updateRequestDto.getAddress());
        company.setCity(CityMapper.fromRequest(updateRequestDto.getCity()));
        company.setPhoneNumber(updateRequestDto.getPhoneNumber());
        company.setDescription(updateRequestDto.getDescription());
        company.setOpeningHours(updateRequestDto.getOpeningHours());
        company.setClosingHours(updateRequestDto.getClosingHours());
        repository.save(company);
        return CompanyMapper.toResponse(company);
    }

    public void updateImages(List<MultipartFile> newImages, List<RemoveImageRequestDto> removedImages) {
        User provider = authService.getCurrentUser();
        Company company = companyRepository.getCompanyByProviderId(provider.getId());
        uploadImages(company.getId(), newImages);
        company.getPhotos().removeIf(image ->
                removedImages.stream().anyMatch(removed -> removed.getId().equals(image.getId()))
        );
        repository.save(company);
    }
}
