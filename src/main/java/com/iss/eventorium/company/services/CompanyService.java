package com.iss.eventorium.company.services;

import com.iss.eventorium.company.dtos.*;
import com.iss.eventorium.company.mappers.CompanyMapper;
import com.iss.eventorium.company.models.Company;
import com.iss.eventorium.company.repositories.CompanyRepository;
import com.iss.eventorium.shared.exceptions.InvalidTimeRangeException;
import com.iss.eventorium.shared.dtos.ImageResponseDto;
import com.iss.eventorium.shared.dtos.RemoveImageRequestDto;
import com.iss.eventorium.shared.mappers.CityMapper;
import com.iss.eventorium.shared.models.ImagePath;
import com.iss.eventorium.shared.services.ImageService;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.services.AccountActivationService;
import com.iss.eventorium.user.services.AuthService;
import com.iss.eventorium.user.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    private static final String IMG_DIR_NAME = "companies";

    public CompanyResponseDto createCompany(CompanyRequestDto companyRequestDto) {
        Company company = mapper.fromRequest(companyRequestDto);
        validateWorkingHours(company);
        User provider = userService.find(companyRequestDto.getProviderId());
        company.setProvider(provider);
        accountActivationService.sendActivationEmail(provider);
        return mapper.toResponse(repository.save(company));
    }

    private void validateWorkingHours(Company company) {
        if (!company.getClosingHours().isAfter(company.getOpeningHours())) {
            String message = String.format(
                    "Invalid working hours: closing time (%s) must be after opening time (%s).",
                    company.getClosingHours(),
                    company.getOpeningHours()
            );
            throw new InvalidTimeRangeException(message);
        }
    }

    public void uploadImages(Long id, List<MultipartFile> images) {
        if (images == null || images.isEmpty()) return;

        Company company = find(id);
        List<ImagePath> paths = imageService.uploadImages(IMG_DIR_NAME, id, images);

        if (!paths.isEmpty()) {
            company.getImagePaths().addAll(paths);
            repository.save(company);
        }
    }

    private Company find(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Company not found."));
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
        return imageService.getImages(IMG_DIR_NAME, find(id));
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

    public void deleteImages(List<RemoveImageRequestDto> removedImages) {
        User provider = authService.getCurrentUser();
        Company company = repository.getCompanyByProviderId(provider.getId());
        company.getImagePaths().removeIf(image ->
                removedImages.stream().anyMatch(removed -> removed.getId().equals(image.getId()))
        );
        repository.save(company);
    }

    public Company getByProviderId(Long id) {
        return repository.getCompanyByProviderId(id);
    }
}
