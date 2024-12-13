package com.iss.eventorium.solution.services;

import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.shared.models.ImagePath;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.shared.utils.ImageUpload;
import com.iss.eventorium.shared.utils.PagedResponse;
import com.iss.eventorium.solution.dtos.services.CreateServiceRequestDto;
import com.iss.eventorium.solution.dtos.services.ServiceResponseDto;
import com.iss.eventorium.solution.dtos.services.ServiceSummaryResponseDto;
import com.iss.eventorium.solution.mappers.ServiceMapper;
import com.iss.eventorium.solution.repositories.ServiceRepository;
import com.iss.eventorium.solution.models.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${image-path}")
    private String imagePath;

    public List<ServiceSummaryResponseDto> getTopServices(){
        Pageable pageable = PageRequest.of(0, 5); // TODO: think about getting pageable object from frontend
        List<Service> services = repository.findTopFiveServices(pageable);
        return services.stream().map(ServiceMapper::toSummaryResponse).toList();
    }

    public PagedResponse<ServiceSummaryResponseDto> getServicesPaged(Pageable pageable) {
        Page<Service> services = repository.findAll(pageable);
        return ServiceMapper.toPagedResponse(services);
    }

    public ServiceResponseDto createService(CreateServiceRequestDto createServiceRequestDto) {
        Service service = ServiceMapper.fromCreateRequest(createServiceRequestDto);
        if(service.getCategory().getId() == null) {
            service.setStatus(Status.PENDING);
            service.getCategory().setSuggested(true);
        } else {
            service.setStatus(Status.ACCEPTED);
            Category category = entityManager.getReference(Category.class, service.getCategory().getId());
            service.setCategory(category);
        }
        repository.save(service);
        return ServiceMapper.toResponse(service);
    }

    public void uploadImages(Long serviceId, List<MultipartFile> images) {
        if(images == null || images.isEmpty()) {
            return;
        }
        Service service = repository.findById(serviceId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Service with id %s not found", serviceId)));
        List<ImagePath> paths = new ArrayList<>();

        for (MultipartFile image : images) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
            String uploadDir = StringUtils.cleanPath(imagePath + "services/" + serviceId + "/");

            try {
                ImageUpload.saveImage(uploadDir, fileName, image);
                paths.add(ImagePath.builder().path(fileName).build());
            } catch (IOException e) {
                System.err.println("Fail to upload image " + fileName + ": " + e.getMessage());
            }
        }
        service.getImagePaths().addAll(paths);
        repository.save(service);
    }

    public List<byte[]> getImages(Long serviceId) {
        Service service = repository.findById(serviceId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Service with id %s not found", serviceId)));

        String uploadDir = StringUtils.cleanPath(imagePath + "services/" + serviceId + "/");
        List<byte[]> images = new ArrayList<>();
        for(ImagePath imagePath : service.getImagePaths()) {
            try {
                File file = new File(uploadDir + imagePath.getPath());
                images.add(Files.readAllBytes(file.toPath()));
            } catch (IOException e) {
                System.err.println("Fail to read image " + imagePath.getPath() + ": " + e.getMessage());
            }
        }
        return images;
    }


    public List<ServiceSummaryResponseDto> getBudgetSuggestions(Long id, Double price) {
        return repository.getBudgetSuggestions(id, price).stream().map(ServiceMapper::toSummaryResponse).toList();
    }
}
