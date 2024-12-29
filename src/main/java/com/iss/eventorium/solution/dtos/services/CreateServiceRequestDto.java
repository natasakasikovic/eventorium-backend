package com.iss.eventorium.solution.dtos.services;

import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.event.dtos.EventTypeResponseDto;
import com.iss.eventorium.solution.models.ReservationType;
import com.iss.eventorium.solution.validators.DurationConstraint;
import com.iss.eventorium.solution.validators.NotInPast;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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

    @NotNull(message = "Price is mandatory")
    @Min(value = 0, message = "Price must be non-negative")
    private Double price;

    @NotNull(message = "Discount is mandatory")
    @Min(value = 0, message = "Discount must be non-negative")
    @Max(value = 100, message = "Discount cannot exceed 100")
    private Double discount;

    @NotEmpty(message = "Event types are mandatory")
    @Valid
    private List<EventTypeResponseDto> eventTypes;

    @NotNull(message = "Category is mandatory")
    @Valid
    private CategoryResponseDto category;

    @NotNull(message = "Reservation type is mandatory")
    private ReservationType type;

    @NotNull(message = "Reservation deadline is mandatory")
    @NotInPast(message = "Reservation deadline cannot be in the past")
    private LocalDate reservationDeadline;

    @NotNull(message = "Cancellation deadline is mandatory")
    @NotInPast(message = "Cancellation deadline cannot be in the past")
    private LocalDate cancellationDeadline;

    @NotNull(message = "Min duration is mandatory")
    @Min(value = 1, message = "Min duration must be at least 1")
    @Max(value = 24, message = "Min duration cannot exceed 100")
    private Integer minDuration;

    @NotNull(message = "Max duration is mandatory")
    @Min(value = 1, message = "Max duration must be at least 1")
    @Max(value = 24, message = "Max duration cannot exceed 100")
    private Integer maxDuration;
}
