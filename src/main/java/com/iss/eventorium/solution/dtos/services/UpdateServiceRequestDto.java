package com.iss.eventorium.solution.dtos.services;

import com.iss.eventorium.solution.models.ReservationType;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateServiceRequestDto {
    private String name;
    private String description;
    private String specialties;
    private Double price;
    private Double discount;
    private List<Long> eventTypesIds;
    private ReservationType type;
    private LocalDate reservationDeadline;
    private LocalDate cancellationDeadline;
    private Integer minDuration;
    private Integer maxDuration;
    private Boolean available;
    private Boolean visible;
}
