package com.iss.eventorium.solution.mappers;

import com.iss.eventorium.category.mappers.CategoryMapper;
import com.iss.eventorium.company.mappers.CompanyMapper;
import com.iss.eventorium.company.models.Company;
import com.iss.eventorium.event.mappers.EventTypeMapper;
import com.iss.eventorium.interaction.models.Rating;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.solution.dtos.services.*;
import com.iss.eventorium.solution.models.Memento;
import com.iss.eventorium.solution.models.Service;
import com.iss.eventorium.user.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class ServiceMapper {

    private final ModelMapper modelMapper;
    private final EventTypeMapper eventTypeMapper;
    private final CategoryMapper categoryMapper;
    private final CompanyMapper companyMapper;
    private final UserMapper userMapper;

    public ServiceDetailsDto toDetailsResponse(Service service, Company company) {
        ServiceDetailsDto dto = modelMapper.map(service, ServiceDetailsDto.class);
        dto.setCategory(categoryMapper.toResponse(service.getCategory()));
        dto.setEventTypes(service.getEventTypes().stream().map(eventTypeMapper::toResponse).toList());
        try {
            dto.setRating(service.getRatings().stream()
                    .mapToInt(Rating::getRating)
                    .average()
                    .orElse(0.0));
        } catch (NullPointerException e) {
            dto.setRating(0.0d);
        }
        dto.setProvider(userMapper.toUserDetails(service.getProvider()));
        dto.setCompany(companyMapper.toResponse(company));
        return dto;
    }

    public Service fromCreateRequest(CreateServiceRequestDto request) {
        return Service.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .discount(request.getDiscount())
                .minDuration(request.getMinDuration())
                .maxDuration(request.getMaxDuration())
                .cancellationDeadline(request.getCancellationDeadline())
                .reservationDeadline(request.getReservationDeadline())
                .isVisible(request.getIsVisible())
                .isDeleted(false)
                .isAvailable(request.getIsAvailable())
                .ratings(new ArrayList<>())
                .specialties(request.getSpecialties())
                .type(request.getType())
                .category(categoryMapper.fromResponse(request.getCategory()))
                .eventTypes(request.getEventTypes().stream().map(eventTypeMapper::fromResponse).toList())
                .build();
    }

    public Service fromUpdateRequest(UpdateServiceRequestDto request, Service toUpdate) {
        Service service = modelMapper.map(request, Service.class);
        service.setIsAvailable(request.getAvailable());
        service.setIsVisible(request.getVisible());
        service.setId(toUpdate.getId());
        service.setStatus(toUpdate.getStatus());
        service.setCategory(toUpdate.getCategory());
        service.setRatings(toUpdate.getRatings());
        service.setImagePaths(toUpdate.getImagePaths());
        service.setIsDeleted(toUpdate.getIsDeleted());
        service.setProvider(toUpdate.getProvider());
        return service;
    }

    public Memento toMemento(Service service) {
        return modelMapper.map(service, Memento.class);
    }

    public ServiceResponseDto toResponse(Service service) {
        ServiceResponseDto dto = modelMapper.map(service, ServiceResponseDto.class);
        dto.setCategory(categoryMapper.toResponse(service.getCategory()));
        dto.setEventTypes(service.getEventTypes().stream().map(eventTypeMapper::toResponse).toList());
        try {
            dto.setRating(service.getRatings().stream()
                    .mapToInt(Rating::getRating)
                    .average()
                    .orElse(0.0));
        } catch (NullPointerException e) {
            dto.setRating(0.0d);
        }
        return dto;
    }

    public ServiceSummaryResponseDto toSummaryResponse(Service service) {
        ServiceSummaryResponseDto dto = modelMapper.map(service, ServiceSummaryResponseDto.class);
        if(service.getRatings() != null) {
            dto.setRating(service.getRatings().stream()
                    .mapToInt(Rating::getRating)
                    .average()
                    .orElse(0.0));
        } else {
            dto.setRating(0.0d);
        }
        return dto;
    }

    public PagedResponse<ServiceSummaryResponseDto> toPagedResponse(Page<Service> page) {
        return new PagedResponse<>(
                page.getContent().stream().map(this::toSummaryResponse).toList(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}
