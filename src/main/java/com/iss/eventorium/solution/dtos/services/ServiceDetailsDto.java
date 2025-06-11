package com.iss.eventorium.solution.dtos.services;

import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.company.dtos.CompanyResponseDto;
import com.iss.eventorium.event.dtos.eventtype.EventTypeResponseDto;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.solution.models.ReservationType;
import com.iss.eventorium.user.dtos.user.UserDetailsDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceDetailsDto {
    private Long id;
    private String name;
    private String description;
    private String specialties;
    private Double price;
    private Double discount;
    private Status status;
    private List<EventTypeResponseDto> eventTypes;
    private Double rating;
    private CategoryResponseDto category;
    private ReservationType type;
    private Integer reservationDeadline;
    private Integer cancellationDeadline;
    private Integer minDuration;
    private Integer maxDuration;
    private Boolean available;
    private Boolean visible;
    private UserDetailsDto provider;
    private CompanyResponseDto company;
}
