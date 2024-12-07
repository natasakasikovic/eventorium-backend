package com.iss.eventorium.solution.mappers;

import com.iss.eventorium.category.mappers.CategoryMapper;
import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.event.mappers.EventTypeMapper;
import com.iss.eventorium.solution.dtos.services.CreateServiceRequestDto;
import com.iss.eventorium.solution.dtos.services.ServiceSummaryResponseDto;
import com.iss.eventorium.solution.dtos.services.ServiceRequestDto;
import com.iss.eventorium.solution.dtos.services.ServiceResponseDto;
import com.iss.eventorium.solution.models.Service;
import com.iss.eventorium.solution.models.Solution;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
                .validFrom(LocalDateTime.now())
                .eventTypes(request.getEventTypes().stream().map(EventTypeMapper::fromResponse).toList())
                .build();
    }

    public static Service fromRequest(ServiceRequestDto request) {
        return modelMapper.map(request, Service.class);
    }

    public static ServiceResponseDto toResponse(Service service) {
        return modelMapper.map(service, ServiceResponseDto.class);
    }

    public static ServiceSummaryResponseDto toSummaryResponse(Service service) {
        return modelMapper.map(service, ServiceSummaryResponseDto.class);
    }

}
