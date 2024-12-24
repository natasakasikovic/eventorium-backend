package com.iss.eventorium.solution.dtos.services;

import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.event.dtos.EventTypeResponseDto;
import com.iss.eventorium.solution.models.ReservationType;
import com.iss.eventorium.solution.validators.DurationConstraint;
import com.iss.eventorium.solution.validators.NotInPast;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DurationConstraint
public class CreateServiceRequestDto {

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotBlank(message = "Specialties are mandatory")
    private String specialties;

    @NotBlank(message = "Price is mandatory")
    private double price;

    @NotBlank(message = "Discount is mandatory")
    private double discount;

    @NotBlank(message = "Event types are mandatory")
    private List<EventTypeResponseDto> eventTypes;

    @NotBlank(message = "Category is mandatory")
    private CategoryResponseDto category;

    @NotBlank(message = "Reservation type is mandatory")
    private ReservationType type;

    @NotBlank(message = "Reservation deadline is mandatory")
    @NotInPast
    private LocalDate reservationDeadline;

    @NotBlank(message = "Cancellation deadline is mandatory")
    @NotInPast
    private LocalDate cancellationDeadline;

    @NotBlank(message = "Min duration is mandatory")
    private Integer minDuration;

    @NotBlank(message = "Max duration is mandatory")
    private Integer maxDuration;
}
