package com.iss.eventorium.solution.services;

import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.category.services.CategoryProposalService;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.company.repositories.CompanyRepository;
import com.iss.eventorium.event.models.EventType;
import com.iss.eventorium.event.repositories.EventTypeRepository;
import com.iss.eventorium.event.services.EventService;
import com.iss.eventorium.shared.dtos.ImageResponseDto;
import com.iss.eventorium.shared.exceptions.ImageNotFoundException;
import com.iss.eventorium.shared.models.ImagePath;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.shared.services.ImageService;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.solution.dtos.services.*;
import com.iss.eventorium.solution.exceptions.ServiceAlreadyReservedException;
import com.iss.eventorium.solution.mappers.ServiceMapper;
import com.iss.eventorium.solution.repositories.ReservationRepository;
import com.iss.eventorium.solution.repositories.ServiceRepository;
import com.iss.eventorium.solution.models.Service;
import com.iss.eventorium.solution.specifications.ServiceSpecification;
import com.iss.eventorium.user.services.AuthService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Slf4j
public class ServiceService {

    private final ServiceRepository repository;
    private final AuthService authService;
    private final EventService eventService;
    private final CompanyRepository companyRepository;
    private final EventTypeRepository eventTypeRepository;
    private final ReservationRepository reservationRepository;
    private final HistoryService historyService;
    private final CategoryProposalService categoryProposalService;
    private final ImageService imageService;

    private final ServiceMapper mapper;
    
    @PersistenceContext
    private EntityManager entityManager;

    @Value("${image-path}")
    private String imagePath;

    private static final String IMG_DIR_NAME = "services";

    // TODO: refactor method below to use specification
    public List<ServiceSummaryResponseDto> getTopServices(){
        Pageable pageable = PageRequest.of(0, 5); // TODO: think about getting pageable object from frontend
        List<Service> services = repository.findTopFiveServices(pageable);
        return services.stream().map(mapper::toSummaryResponse).toList();
    }

    public ServiceDetailsDto getService(Long id) {
        Service service = find(id);
        return mapper.toDetailsResponse(service, companyRepository.getCompanyByProviderId(service.getProvider().getId()));
    }

    public List<ServiceSummaryResponseDto> getServices() {
        Specification<Service> specification = ServiceSpecification.filter(authService.getCurrentUser());
        return repository.findAll(specification).stream().map(mapper::toSummaryResponse).toList();
    }

    public PagedResponse<ServiceSummaryResponseDto> getServicesPaged(Pageable pageable) {
        Specification<Service> specification = ServiceSpecification.filter(authService.getCurrentUser());
        return mapper.toPagedResponse(repository.findAll(specification, pageable));
    }

    public List<ServiceSummaryResponseDto> searchServices(String keyword) {
        Specification<Service> specification = ServiceSpecification.filterByName(keyword, authService.getCurrentUser());
        return repository.findAll(specification).stream().map(mapper::toSummaryResponse).toList();
    }

    public PagedResponse<ServiceSummaryResponseDto> searchServices(String keyword, Pageable pageable) {
        Specification<Service> specification = ServiceSpecification.filterByName(keyword, authService.getCurrentUser());
        return mapper.toPagedResponse(repository.findAll(specification, pageable));
    }

    public List<ServiceSummaryResponseDto> filter(ServiceFilterDto filter) {
        Specification<Service> specification = ServiceSpecification.filterBy(filter, authService.getCurrentUser());
        return repository.findAll(specification).stream().map(mapper::toSummaryResponse).toList();
    }

    public PagedResponse<ServiceSummaryResponseDto> filter(ServiceFilterDto filter, Pageable pageable) {
        Specification<Service> specification = ServiceSpecification.filterBy(filter, authService.getCurrentUser());
        return mapper.toPagedResponse(repository.findAll(specification, pageable));
    }

    public ServiceResponseDto createService(CreateServiceRequestDto createServiceRequestDto) {
        Service service = mapper.fromCreateRequest(createServiceRequestDto);
        if(service.getCategory().getId() == null) {
            service.setStatus(Status.PENDING);
            categoryProposalService.handleCategoryProposal(service.getCategory());
        } else {
            service.setStatus(Status.ACCEPTED);
            Category category = entityManager.getReference(Category.class, service.getCategory().getId());
            service.setCategory(category);
        }
        service.setProvider(authService.getCurrentUser());

        historyService.addServiceMemento(service);
        repository.save(service);
        return mapper.toResponse(service);
    }

    public void uploadImages(Long serviceId, List<MultipartFile> images) {
        if (images == null || images.isEmpty())
            return;

        Service service = find(serviceId);
        List<ImagePath> paths = new ArrayList<>();

        for (MultipartFile image : images) {
            String name = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
            String fileName = Instant.now().toEpochMilli() + "_" + name;
            String uploadDir = StringUtils.cleanPath(imagePath + "services/" + serviceId + "/");

            try {
                imageService.uploadImage(uploadDir, fileName, image);
                String contentType = imageService.getImageContentType(uploadDir, fileName);
                paths.add(ImagePath.builder().path(fileName).contentType(contentType).build());
            } catch (IOException e) {
                log.error("Failed to upload image {}: {}", fileName, e.getMessage(), e);
            }
        }
        service.getImagePaths().addAll(paths);
        repository.save(service);
    }

    public List<ImageResponseDto> getImages(Long serviceId) {
        Service service = find(serviceId);

        List<ImageResponseDto> images = new ArrayList<>();
        for(ImagePath path : service.getImagePaths()) {
            byte[] image = getImage(serviceId, path);
            images.add(new ImageResponseDto(image, path.getContentType()));
        }
        return images;
    }

    // TODO: refactor method below to use specification
    public List<ServiceSummaryResponseDto> getBudgetSuggestions(Long id, Long eventId, Double price) {
        Event event = eventService.find(eventId);
        return repository
                .getSuggestedServices(id, price)
                .stream()
                .filter(service -> LocalDate.now().isBefore(event.getDate().minusDays(service.getReservationDeadline())))
                .map(mapper::toSummaryResponse)
                .toList();
    }

    public Service find(Long id) {
        Specification<Service> specification = ServiceSpecification.filterById(id, authService.getCurrentUser());
        return repository.findOne(specification).orElseThrow(() -> new EntityNotFoundException("Service not found"));
    }

    public ImagePath getImagePath(Long serviceId) {
        Service service = find(serviceId);

        if (service.getImagePaths().isEmpty())
            throw new ImageNotFoundException("Image not found");

        return service.getImagePaths().get(0);
    }

    public byte[] getImage(Long id, ImagePath path) {
        return imageService.getImage(IMG_DIR_NAME, id, path);
    }

    public ServiceResponseDto updateService(Long id, UpdateServiceRequestDto request) {
        Service toUpdate = find(id);

        List<EventType> eventTypes = eventTypeRepository.findAllById(request.getEventTypesIds());

        if (eventTypes.size() != request.getEventTypesIds().size())
            throw new EntityNotFoundException("Event types not found.");

        Service service = mapper.fromUpdateRequest(request, toUpdate);
        service.setEventTypes(eventTypes);

        historyService.addServiceMemento(service);
        repository.save(service);
        return mapper.toResponse(service);
    }

    public void deleteService(Long id) {
        Service service = find(id);

        if (reservationRepository.existsByServiceId(id))
            throw new ServiceAlreadyReservedException("The service cannot be deleted because it is currently reserved.");

        service.setIsDeleted(true);
        repository.save(service);
    }
}
