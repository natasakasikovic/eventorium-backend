package com.iss.eventorium.company.services;

import com.iss.eventorium.company.dtos.CompanyRequestDto;
import com.iss.eventorium.company.dtos.CompanyResponseDto;
import com.iss.eventorium.company.mappers.CompanyMapper;
import com.iss.eventorium.company.models.Company;
import com.iss.eventorium.company.repositories.CompanyRepository;
import com.iss.eventorium.shared.exceptions.ImageUploadException;
import com.iss.eventorium.shared.models.ImagePath;
import com.iss.eventorium.shared.utils.ImageUpload;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.services.AccountActivationService;
import com.iss.eventorium.user.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

}
