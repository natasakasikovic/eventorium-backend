package com.iss.eventorium.solution.services;


import com.iss.eventorium.shared.exceptions.AlreadyInFavoritesException;
import com.iss.eventorium.shared.utils.PagedResponse;
import com.iss.eventorium.solution.dtos.services.ServiceFilterDto;
import com.iss.eventorium.solution.dtos.services.ServiceResponseDto;
import com.iss.eventorium.solution.dtos.services.ServiceSummaryResponseDto;
import com.iss.eventorium.solution.mappers.ServiceMapper;
import com.iss.eventorium.solution.models.Service;
import com.iss.eventorium.solution.repositories.ServiceRepository;
import com.iss.eventorium.solution.repositories.ServiceSpecification;
import com.iss.eventorium.user.repositories.UserRepository;
import com.iss.eventorium.user.services.AuthService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.iss.eventorium.solution.mappers.ServiceMapper.toPagedResponse;
import static com.iss.eventorium.solution.mappers.ServiceMapper.toSummaryResponse;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class AccountServiceService {

    private final AuthService authService;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    public List<ServiceSummaryResponseDto> getServices() {
        return serviceRepository.findByProvider_Id(authService.getCurrentUser().getId()).stream()
                .map(ServiceMapper::toSummaryResponse)
                .toList();
    }

    public PagedResponse<ServiceSummaryResponseDto> getServicesPaged(Pageable pageable) {
        return toPagedResponse(serviceRepository.findByProvider_Id(authService.getCurrentUser().getId(), pageable));
    }

    public List<ServiceSummaryResponseDto> filterServices(ServiceFilterDto filter) {
        return serviceRepository.findAll(
            ServiceSpecification.filterBy(filter, authService.getCurrentUser().getId())
        ).stream().map(ServiceMapper::toSummaryResponse).toList();
    }

    public PagedResponse<ServiceSummaryResponseDto> filterServicesPaged(ServiceFilterDto filter, Pageable pageable) {
        return toPagedResponse(serviceRepository.findAll(
            ServiceSpecification.filterBy(filter, authService.getCurrentUser().getId()),
            pageable
        ));
    }

    public PagedResponse<ServiceSummaryResponseDto> searchServicesPaged(String keyword, Pageable pageable) {
        return toPagedResponse(serviceRepository.findAll(
            ServiceSpecification.search(keyword, authService.getCurrentUser().getId()),
            pageable
        ));
    }

    public List<ServiceSummaryResponseDto> searchServices(String keyword) {
        return serviceRepository.findAll(
                ServiceSpecification.search(keyword, authService.getCurrentUser().getId())
        ).stream().map(ServiceMapper::toSummaryResponse).toList();
    }

    public List<ServiceSummaryResponseDto> getFavouriteServices() {
        return authService.getCurrentUser()
                .getPerson()
                .getFavouriteServices()
                .stream()
                .map(ServiceMapper::toSummaryResponse)
                .toList();
    }

    public ServiceResponseDto addFavouriteService(Long id) {
        Service service = serviceRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Service with id " + id + " not found")
        );
        List<Service> favouriteService = authService.getCurrentUser().getPerson().getFavouriteServices();
        if(favouriteService.contains(service)) {
            throw new AlreadyInFavoritesException("This service is already in your favorites list!");
        }
        favouriteService.add(service);
        userRepository.save(authService.getCurrentUser());
        return ServiceMapper.toResponse(service);
    }

    public void removeFavouriteService(Long id) {
        Service service = serviceRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Service with id " + id + " not found")
        );
        List<Service> favouriteService = authService.getCurrentUser().getPerson().getFavouriteServices();
        if(!favouriteService.contains(service)) {
            throw new EntityNotFoundException("Service with id " + id + " not found in list of favourite services");
        }
        favouriteService.remove(service);
        userRepository.save(authService.getCurrentUser());
    }

    public Boolean isFavouriteService(Long id) {
        Service service = serviceRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Service with id " + id + " not found")
        );
        return authService.getCurrentUser().getPerson().getFavouriteServices().contains(service);
    }
}
