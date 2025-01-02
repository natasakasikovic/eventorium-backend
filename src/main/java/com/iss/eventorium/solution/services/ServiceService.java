package com.iss.eventorium.solution.services;

import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.event.models.EventType;
import com.iss.eventorium.event.repositories.EventTypeRepository;
import com.iss.eventorium.shared.dtos.ImageResponseDto;
import com.iss.eventorium.shared.exceptions.ImageNotFoundException;
import com.iss.eventorium.shared.models.ImagePath;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.shared.utils.ImageUpload;
import com.iss.eventorium.shared.utils.PagedResponse;
import com.iss.eventorium.solution.dtos.services.*;
import com.iss.eventorium.solution.exceptions.ServiceAlreadyReservedException;
import com.iss.eventorium.solution.mappers.ServiceMapper;
import com.iss.eventorium.solution.repositories.ReservationRepository;
import com.iss.eventorium.solution.repositories.ServiceRepository;
import com.iss.eventorium.solution.models.Service;
import com.iss.eventorium.user.services.AuthService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.iss.eventorium.solution.mappers.ServiceMapper.toResponse;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceService.class);
    private final AuthService authService;

    private final ServiceRepository serviceRepository;
    private final EventTypeRepository eventTypeRepository;
    private final ReservationRepository reservationRepository;
    private final HistoryService historyService;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${image-path}")
    private String imagePath;

    public List<ServiceSummaryResponseDto> getTopServices(){
        Pageable pageable = PageRequest.of(0, 5); // TODO: think about getting pageable object from frontend
        List<Service> services = serviceRepository.findTopFiveServices(pageable);
        return services.stream().map(ServiceMapper::toSummaryResponse).toList();
    }

    public List<ServiceSummaryResponseDto> getServices() {
        List<Service> services =  serviceRepository.findAll();
        return services.stream().map(ServiceMapper::toSummaryResponse).toList();
    }

    public PagedResponse<ServiceSummaryResponseDto> getServicesPaged(Pageable pageable) {
        Page<Service> services = serviceRepository.findAll(pageable);
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
        service.setProvider(authService.getCurrentUser());

        historyService.addServiceMemento(service);
        serviceRepository.save(service);
        return toResponse(service);
    }

    public void uploadImages(Long serviceId, List<MultipartFile> images) {
        if(images == null || images.isEmpty()) {
            return;
        }
        Service service = serviceRepository.findById(serviceId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Service with id %s not found", serviceId)));
        List<ImagePath> paths = new ArrayList<>();

        for (MultipartFile image : images) {
            String name = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
            String fileName = Instant.now().toEpochMilli() + "_" + name;
            String uploadDir = StringUtils.cleanPath(imagePath + "services/" + serviceId + "/");

            try {
                ImageUpload.saveImage(uploadDir, fileName, image);
                String contentType = ImageUpload.getImageContentType(uploadDir, fileName);
                paths.add(ImagePath.builder().path(fileName).contentType(contentType).build());
            } catch (IOException e) {
                logger.error("Failed to upload image {}: {}", fileName, e.getMessage(), e);
            }
        }
        service.getImagePaths().addAll(paths);
        serviceRepository.save(service);
    }

    public List<ImageResponseDto> getImages(Long serviceId) {
        Service service = serviceRepository.findById(serviceId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Service with id %s not found", serviceId)));

        List<ImageResponseDto> images = new ArrayList<>();
        for(ImagePath imagePath : service.getImagePaths()) {
            byte[] image = getImage(serviceId, imagePath);
            images.add(new ImageResponseDto(image, imagePath.getContentType()));
        }
        return images;
    }


    public List<ServiceSummaryResponseDto> getBudgetSuggestions(Long id, Double price) {
        return serviceRepository.getBudgetSuggestions(id, price).stream().map(ServiceMapper::toSummaryResponse).toList();
    }

    public ImagePath getImagePath(Long serviceId) {
        Service service = serviceRepository.findById(serviceId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Service with id %s not found", serviceId)));
        if(service.getImagePaths().isEmpty()) {
            throw new ImageNotFoundException("Image not found");
        }
        return service.getImagePaths().get(0);
    }

    public byte[] getImage(Long serviceId, ImagePath path) {
        String uploadDir = StringUtils.cleanPath(imagePath + "services/" + serviceId + "/");
        try {
            File file = new File(uploadDir + path.getPath());
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new ImageNotFoundException("Fail to read image" + path.getPath() + ": + e.getMessage()");
        }
    }

    public ServiceResponseDto getService(Long id) {
        return toResponse(serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Service with id %s not found", id))));
    }

    public PagedResponse<ServiceSummaryResponseDto> searchServices(String keyword, Pageable pageable) {
        if (keyword.isBlank())
            return ServiceMapper.toPagedResponse(serviceRepository.findAll(pageable));
        return ServiceMapper.toPagedResponse(serviceRepository.findByNameContainingAllIgnoreCase(keyword, pageable));
    }

    public ServiceResponseDto updateService(Long id, UpdateServiceRequestDto serviceDto) {
        Service toUpdate = serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Service with id %s not found", id)));

        List<EventType> eventTypes = eventTypeRepository.findAllById(serviceDto.getEventTypesIds());
        if (eventTypes.size() != serviceDto.getEventTypesIds().size()) {
            throw new EntityNotFoundException("One or more event type IDs are invalid.");
        }

        Service service = ServiceMapper.fromUpdateRequest(serviceDto, toUpdate);
        service.setEventTypes(eventTypes);

        historyService.addServiceMemento(service);
        serviceRepository.save(service);
        return toResponse(service);
    }

    public void deleteService(Long id) {
        Service service = serviceRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Service with id %s not found", id)));
        if(reservationRepository.existsByServiceId(id)) {
            throw new ServiceAlreadyReservedException("The service cannot be deleted because it is currently reserved.");
        }
        service.setIsDeleted(true);
        serviceRepository.save(service);
    }

    public List<ServiceSummaryResponseDto> searchServices(String keyword) {
        List<Service> services = keyword.isBlank()
                ? serviceRepository.findAll()
                : serviceRepository.findByNameContainingAllIgnoreCase(keyword);

        return services.stream().map(ServiceMapper::toSummaryResponse).toList();
    }

}
