package com.iss.eventorium.solution.mappers;

import com.iss.eventorium.category.mappers.CategoryMapper;
import com.iss.eventorium.event.mappers.EventTypeMapper;
import com.iss.eventorium.interaction.models.Review;
import com.iss.eventorium.shared.utils.PagedResponse;
import com.iss.eventorium.solution.dtos.services.*;
import com.iss.eventorium.solution.models.Memento;
import com.iss.eventorium.solution.models.Service;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class ServiceMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public ServiceMapper(ModelMapper modelMapper) {
        ServiceMapper.modelMapper = modelMapper;
    }

    public static Service fromCreateRequest(CreateServiceRequestDto request) {
        return Service.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .discount(request.getDiscount())
                .minDuration(request.getMinDuration())
                .maxDuration(request.getMaxDuration())
                .cancellationDeadline(request.getCancellationDeadline())
                .reservationDeadline(request.getReservationDeadline())
                .isVisible(true)
                .isDeleted(false)
                .isAvailable(true)
                .reviews(new ArrayList<>())
                .specialties(request.getSpecialties())
                .type(request.getType())
                .category(CategoryMapper.fromResponse(request.getCategory()))
                .eventTypes(request.getEventTypes().stream().map(EventTypeMapper::fromResponse).toList())
                .build();
    }

    public static Service fromRequest(ServiceRequestDto request) {
        return modelMapper.map(request, Service.class);
    }

    public static Service fromUpdateRequest(UpdateServiceRequestDto request, Service toUpdate) {
        Service service = modelMapper.map(request, Service.class);
        service.setId(toUpdate.getId());
        service.setStatus(toUpdate.getStatus());
        service.setCategory(toUpdate.getCategory());
        service.setReviews(toUpdate.getReviews());
        service.setImagePaths(toUpdate.getImagePaths());
        service.setIsDeleted(toUpdate.getIsDeleted());
        return service;
    }

    public static Memento toMemento(Service service) {
        return modelMapper.map(service, Memento.class);
    }

    public static ServiceResponseDto toResponse(Service service) {
        ServiceResponseDto dto = modelMapper.map(service, ServiceResponseDto.class);
        dto.setCategory(CategoryMapper.toResponse(service.getCategory()));
        dto.setEventTypes(service.getEventTypes().stream().map(EventTypeMapper::toResponse).toList());
        try {
            dto.setRating(service.getReviews().stream().mapToInt(Review::getRating).average().orElse(0.0));
        } catch (NullPointerException e) {
            dto.setRating(0.0d);
        }
        return dto;
    }

    public static ServiceSummaryResponseDto toSummaryResponse(Service service) {
        ServiceSummaryResponseDto dto = modelMapper.map(service, ServiceSummaryResponseDto.class);
        try {
            dto.setRating(service.getReviews().stream().mapToInt(Review::getRating).average().orElse(0.0));
        } catch (NullPointerException e) {
            dto.setRating(0.0d);
        }
        return dto;
    }

    public static PagedResponse<ServiceSummaryResponseDto> toPagedResponse(Page<Service> page) {
        return new PagedResponse<>(
                page.stream().map(ServiceMapper::toSummaryResponse).toList(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}
