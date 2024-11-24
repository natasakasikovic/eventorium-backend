package com.iss.eventorium.service.dtos;

import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.eventtype.dtos.EventTypeResponseDto;
import com.iss.eventorium.service.models.ReservationType;
import com.iss.eventorium.shared.models.EventType;
import com.iss.eventorium.shared.models.Status;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceResponseDto {
    private Long id;
    private String name;
    private String description;
    private String specialties;
    private Double price;
    private Double discount;
    private Status status;
    private List<EventTypeResponseDto> eventTypes;
    private CategoryResponseDto category;
    private ReservationType type;
    private LocalDate reservationDeadline;
    private LocalDate cancellationDeadline;
    private Integer minDuration;
    private Integer maxDuration;
}
