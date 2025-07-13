package com.iss.eventorium.solution.services;

import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.category.services.CategoryProposalService;
import com.iss.eventorium.company.services.CompanyService;
import com.iss.eventorium.event.models.EventType;
import com.iss.eventorium.event.services.EventService;
import com.iss.eventorium.event.services.EventTypeService;
import com.iss.eventorium.shared.dtos.ImageResponseDto;
import com.iss.eventorium.shared.dtos.RemoveImageRequestDto;
import com.iss.eventorium.shared.exceptions.ImageNotFoundException;
import com.iss.eventorium.shared.exceptions.OwnershipRequiredException;
import com.iss.eventorium.shared.models.ImagePath;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.shared.services.ImageService;
import com.iss.eventorium.solution.dtos.services.*;
import com.iss.eventorium.solution.exceptions.ServiceAlreadyReservedException;
import com.iss.eventorium.solution.mappers.ServiceMapper;
import com.iss.eventorium.solution.models.Service;
import com.iss.eventorium.solution.repositories.ReservationRepository;
import com.iss.eventorium.solution.repositories.ServiceRepository;
import com.iss.eventorium.solution.specifications.ServiceSpecification;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.services.AuthService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Slf4j
public class ServiceService {

    private final ServiceRepository repository;
    private final AuthService authService;
    private final EventService eventService;
    private final CompanyService companyService;
    private final EventTypeService eventTypeService;
    private final ReservationRepository reservationRepository;
    private final HistoryService historyService;
    private final CategoryProposalService categoryProposalService;
    private final ImageService imageService;

    private final ServiceMapper mapper;
    
    @PersistenceContext
    private EntityManager entityManager;

    private static final String IMG_DIR_NAME = "services";

    public List<ServiceSummaryResponseDto> getTopFiveServices() {
        Specification<Service> specification = ServiceSpecification.filterTopServices(authService.getCurrentUser());
        List<Service> services = repository.findAll(specification).stream().limit(5).toList();
        return services.stream().map(mapper::toSummaryResponse).toList();
    }

    public ServiceDetailsDto getService(Long id) {
        Service service = find(id);
        return mapper.toDetailsResponse(service, companyService.getByProviderId(service.getProvider().getId()));
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
        handleCategoryAndStatus(service);
        service.setProvider(authService.getCurrentUser());

        repository.save(service);
        historyService.addMemento(service);
        return mapper.toResponse(service);
    }

    public void uploadImages(Long id, List<MultipartFile> images) {
        if (images == null || images.isEmpty()) return;
        Service service = find(id);
        assertOwnership(service);

        List<ImagePath> paths = imageService.uploadImages(IMG_DIR_NAME, id, images);

        if (!paths.isEmpty()) {
            service.getImagePaths().addAll(paths);
            repository.save(service);
        }
    }

    public List<ImageResponseDto> getImages(Long id) {
        return imageService.getImages(IMG_DIR_NAME, find(id));
    }

    public Service find(Long id) {
        Specification<Service> specification = ServiceSpecification.filterById(id, authService.getCurrentUser(), false);
        return repository.findOne(specification).orElseThrow(() -> new EntityNotFoundException("Service not found"));
    }

    public Optional<Service> findEvenIfDeleted(Long id) {
        Specification<Service> specification = ServiceSpecification.filterById(id, authService.getCurrentUser(), true);
        return repository.findOne(specification);
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
        assertOwnership(toUpdate);

        List<EventType> eventTypes = eventTypeService.findAllById(request.getEventTypesIds());

        if (eventTypes.size() != request.getEventTypesIds().size())
            throw new EntityNotFoundException("Event types not found.");

        Service service = mapper.fromUpdateRequest(request, toUpdate);
        service.setEventTypes(eventTypes);

        historyService.addMemento(service);
        repository.save(service);
        return mapper.toResponse(service);
    }

    public void deleteService(Long id) {
        Service service = find(id);
        assertOwnership(service);

        if (reservationRepository.existsByServiceId(id))
            throw new ServiceAlreadyReservedException("The service cannot be deleted because it is currently reserved.");

        service.setIsDeleted(true);
        repository.save(service);
    }

    public void deleteImages(Long id, List<RemoveImageRequestDto> removedImages) {
        Service service = find(id);
        assertOwnership(service);

        service.getImagePaths().removeIf(image ->
                removedImages.stream().anyMatch(removed -> removed.getId().equals(image.getId()))
        );
        repository.save(service);
    }


    private void handleCategoryAndStatus(Service service) {
        if(service.getCategory().getId() == null) {
            service.setStatus(Status.PENDING);
            categoryProposalService.handleCategoryProposal(service.getCategory());
        } else {
            service.setStatus(Status.ACCEPTED);
            Category category = entityManager.getReference(Category.class, service.getCategory().getId());
            service.setCategory(category);
        }
    }

    private void assertOwnership(Service service) {
        User provider = authService.getCurrentUser();
        if(!Objects.equals(provider.getId(), service.getProvider().getId())) {
            throw new OwnershipRequiredException("You are not authorized to change this service.");
        }
    }
}
