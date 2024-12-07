package com.iss.eventorium.solution.services;

import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.solution.dtos.services.CreateServiceRequestDto;
import com.iss.eventorium.solution.dtos.services.ServiceResponseDto;
import com.iss.eventorium.solution.dtos.services.ServiceSummaryResponseDto;
import com.iss.eventorium.solution.mappers.ServiceMapper;
import com.iss.eventorium.solution.repositories.ServiceRepository;
import com.iss.eventorium.solution.models.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<ServiceSummaryResponseDto> getTopServices(){
        Pageable pageable = PageRequest.of(0, 5); // TODO: think about getting pageable object from frontend
        List<Service> services = repository.findTopFiveServices(pageable);
        return services.stream().map(ServiceMapper::toSummaryResponse).toList();
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
}
