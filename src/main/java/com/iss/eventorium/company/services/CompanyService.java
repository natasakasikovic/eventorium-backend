package com.iss.eventorium.company.services;

import com.iss.eventorium.company.dtos.*;
import com.iss.eventorium.company.mappers.CompanyMapper;
import com.iss.eventorium.company.models.Company;
import com.iss.eventorium.company.repositories.CompanyRepository;
import com.iss.eventorium.shared.dtos.ImageResponseDto;
import com.iss.eventorium.shared.dtos.RemoveImageRequestDto;
import com.iss.eventorium.shared.exceptions.ImageNotFoundException;
import com.iss.eventorium.shared.exceptions.ImageUploadException;
import com.iss.eventorium.shared.mappers.CityMapper;
import com.iss.eventorium.shared.models.ImagePath;
import com.iss.eventorium.shared.services.ImageService;
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
    private final ImageService imageService;

    private final CompanyMapper mapper;
    private final CityMapper cityMapper;

    @Value("${image-path}")
    private String imagePath;

    private static final String IMG_DIR_NAME = "companies";

    public CompanyResponseDto createCompany(CompanyRequestDto companyRequestDto) {
        Company company = mapper.fromRequest(companyRequestDto);
        User provider = userService.find(companyRequestDto.getProviderId());
        company.setProvider(provider);
        accountActivationService.sendActivationEmail(provider);
        return mapper.toResponse(repository.save(company));
    }

    public void uploadImages(Long id, List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return;
        }

        Company company = find(id);
        List<ImagePath> paths = processImages(id, images);

        if (!paths.isEmpty()) {
            company.getImagePaths().addAll(paths);
            repository.save(company);
        }
    }

    private Company find(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Company not found."));
    }

    private List<ImagePath> processImages(Long companyId, List<MultipartFile> images) {
        List<ImagePath> paths = new ArrayList<>();
        String uploadDir = StringUtils.cleanPath(imagePath + "companies/" + companyId + "/");

        for (MultipartFile image : images) {
            try {
                ImagePath path = saveImage(uploadDir, image);
                if (path != null) {
                    paths.add(path);
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

        imageService.uploadImage(uploadDir, fileName, image);
        String contentType = imageService.getImageContentType(uploadDir, fileName);

        return ImagePath.builder().path(fileName).contentType(contentType).build();
    }

    public ProviderCompanyDto getCompany() {
        User currentUser = authService.getCurrentUser();
        Company company = repository.getCompanyByProviderId(currentUser.getId());
        return mapper.toProviderCompanyResponse(company);
    }

    public CompanyDetailsDto getCompany(Long id) {
        Company company = find(id);
        return mapper.toCompanyDetailsResponse(company);
    }

    public List<ImageResponseDto> getImages(Long id) {
        return imageService.getImages(IMG_DIR_NAME, id, this::find);
    }

    public byte[] getImage(Long id, ImagePath path) {
        return imageService.getImage(IMG_DIR_NAME, id, path);
    }

    public CompanyResponseDto updateCompany(UpdateCompanyRequestDto updateRequestDto) {
        Company company = find(updateRequestDto.getId());
        company.setAddress(updateRequestDto.getAddress());
        company.setCity(cityMapper.fromRequest(updateRequestDto.getCity()));
        company.setPhoneNumber(updateRequestDto.getPhoneNumber());
        company.setDescription(updateRequestDto.getDescription());
        company.setOpeningHours(updateRequestDto.getOpeningHours());
        company.setClosingHours(updateRequestDto.getClosingHours());
        repository.save(company);
        return mapper.toResponse(company);
    }

    public void uploadNewImages(List<MultipartFile> newImages) {
        User provider = authService.getCurrentUser();
        Company company = repository.getCompanyByProviderId(provider.getId());
        uploadImages(company.getId(), newImages);
        repository.save(company);
    }

    public void removeImages(List<RemoveImageRequestDto> removedImages) {
        User provider = authService.getCurrentUser();
        Company company = repository.getCompanyByProviderId(provider.getId());
        company.getImagePaths().removeIf(image ->
                removedImages.stream().anyMatch(removed -> removed.getId().equals(image.getId()))
        );
        repository.save(company);
    }
}
