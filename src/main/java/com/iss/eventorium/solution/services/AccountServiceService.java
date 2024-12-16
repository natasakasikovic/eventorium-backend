package com.iss.eventorium.solution.services;


import com.iss.eventorium.shared.utils.PagedResponse;
import com.iss.eventorium.solution.dtos.services.ServiceFilterDto;
import com.iss.eventorium.solution.dtos.services.ServiceSummaryResponseDto;
import com.iss.eventorium.solution.mappers.ServiceMapper;
import com.iss.eventorium.solution.repositories.ServiceRepository;
import com.iss.eventorium.solution.repositories.ServiceSpecification;
import com.iss.eventorium.user.services.AuthService;
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
}
