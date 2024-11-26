package com.iss.eventorium.service.dtos;

import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.event.dtos.EventTypeResponseDto;
import com.iss.eventorium.service.models.ReservationType;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateServiceRequestDto {
    private String name;
    private String description;
    private String specialties;
    private double price;
    private double discount;
    private List<EventTypeResponseDto> eventTypes;
    private CategoryResponseDto category;
    private ReservationType type;
    private LocalDate reservationDeadline;
    private LocalDate cancellationDeadline;
    private Integer minDuration;
    private Integer maxDuration;
}
