package com.iss.eventorium.solution.services;


import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.solution.dtos.services.ServiceFilterDto;
import com.iss.eventorium.solution.dtos.services.ServiceSummaryResponseDto;
import com.iss.eventorium.solution.mappers.ServiceMapper;
import com.iss.eventorium.solution.models.Service;
import com.iss.eventorium.solution.repositories.ServiceRepository;
import com.iss.eventorium.solution.specifications.ServiceSpecification;
import com.iss.eventorium.user.repositories.UserRepository;
import com.iss.eventorium.user.services.AuthService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static com.iss.eventorium.solution.mappers.ServiceMapper.toPagedResponse;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class AccountServiceService {

    private final ServiceRepository repository;
    private final AuthService authService;
    private final UserRepository userRepository;

    public List<ServiceSummaryResponseDto> getServices() {
        Specification<Service> specification = ServiceSpecification.filterForProvider(authService.getCurrentUser());
        return repository.findAll(specification).stream().map(ServiceMapper::toSummaryResponse).toList();
    }

    public PagedResponse<ServiceSummaryResponseDto> getServicesPaged(Pageable pageable) {
        Specification<Service> specification = ServiceSpecification.filterForProvider(authService.getCurrentUser());
        return toPagedResponse(repository.findAll(specification, pageable));
    }

    public List<ServiceSummaryResponseDto> filterServices(ServiceFilterDto filter) {
        Specification<Service> specification = ServiceSpecification.filterByForProvider(filter, authService.getCurrentUser());
        return repository.findAll(specification).stream().map(ServiceMapper::toSummaryResponse).toList();
    }

    public PagedResponse<ServiceSummaryResponseDto> filterServicesPaged(ServiceFilterDto filter, Pageable pageable) {
        Specification<Service> specification = ServiceSpecification.filterByForProvider(filter, authService.getCurrentUser());
        return toPagedResponse(repository.findAll(specification, pageable));
    }

    public PagedResponse<ServiceSummaryResponseDto> searchServicesPaged(String keyword, Pageable pageable) {
        Specification<Service> specification = ServiceSpecification.filterByNameForProvider(keyword, authService.getCurrentUser());
        return toPagedResponse(repository.findAll(specification, pageable));
    }

    public List<ServiceSummaryResponseDto> searchServices(String keyword) {
        Specification<Service> specification = ServiceSpecification.filterByNameForProvider(keyword, authService.getCurrentUser());
        return repository.findAll(specification).stream().map(ServiceMapper::toSummaryResponse).toList();
    }

    public List<ServiceSummaryResponseDto> getFavouriteServices() {
        return authService.getCurrentUser()
                .getPerson()
                .getFavouriteServices()
                .stream()
                .map(ServiceMapper::toSummaryResponse)
                .toList();
    }

    public void addFavouriteService(Long id) {
        Service service = find(id);

        List<Service> favouriteService = authService.getCurrentUser().getPerson().getFavouriteServices();
        if(!favouriteService.contains(service)) {
            favouriteService.add(service);
            userRepository.save(authService.getCurrentUser());
        }
    }

    public void removeFavouriteService(Long id) {

        Service service = find(id);

        List<Service> favouriteService = authService.getCurrentUser().getPerson().getFavouriteServices();
        if(favouriteService.contains(service)) {
            favouriteService.remove(service);
            userRepository.save(authService.getCurrentUser());
        }
    }

    public Service find(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Service not found."));
    }

    public Boolean isFavouriteService(Long id) {
        Service service = find(id);
        return authService.getCurrentUser().getPerson().getFavouriteServices().contains(service);
    }
}
