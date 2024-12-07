package com.iss.eventorium.solution.services;

import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.solution.dtos.services.CreateServiceRequestDto;
import com.iss.eventorium.solution.dtos.services.ServiceResponseDto;
import com.iss.eventorium.solution.dtos.services.ServiceSummaryResponseDto;
import com.iss.eventorium.solution.mappers.ServiceMapper;
import com.iss.eventorium.solution.repositories.ServiceRepository;
import com.iss.eventorium.solution.models.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository repository;

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
        }
        repository.save(service);
        return ServiceMapper.toResponse(service);
    }
}
