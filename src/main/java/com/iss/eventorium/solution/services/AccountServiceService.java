package com.iss.eventorium.solution.services;


import com.iss.eventorium.shared.utils.PagedResponse;
import com.iss.eventorium.solution.dtos.services.ServiceSummaryResponseDto;
import com.iss.eventorium.solution.mappers.ServiceMapper;
import com.iss.eventorium.solution.repositories.ServiceRepository;
import com.iss.eventorium.user.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.iss.eventorium.solution.mappers.ServiceMapper.toPagedResponse;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class AccountServiceService {

    private final AuthService authService;
    private final ServiceRepository serviceRepository;

    public List<ServiceSummaryResponseDto> getServices() {
        return serviceRepository.findByProviderId(authService.getCurrentUser().getId()).stream()
                .map(ServiceMapper::toSummaryResponse)
                .toList();
    }

    public PagedResponse<ServiceSummaryResponseDto> getServicesPaged(Pageable pageable) {
        return toPagedResponse(serviceRepository.findByProviderId(authService.getCurrentUser().getId(), pageable));
    }

    public List<ServiceSummaryResponseDto> filterServices() {
        return null;
    }

    public PagedResponse<ServiceSummaryResponseDto> filterServicesPaged(Pageable pageable) {
        return null;
    }
}
