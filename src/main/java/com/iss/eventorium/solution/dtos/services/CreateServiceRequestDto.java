package com.iss.eventorium.solution.dtos.services;

import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.event.dtos.eventtype.EventTypeResponseDto;
import com.iss.eventorium.solution.models.ReservationType;
import com.iss.eventorium.solution.validators.DurationConstraint;
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
    @Size(min = 1, max = 75, message = "Name must be 75 characters or fewer")
    private String name;

    @NotBlank(message = "Description is mandatory")
    @Size(min = 1, max = 750, message = "Description must be 750 characters or fewer")
    private String description;

    @NotBlank(message = "Specialties are mandatory")
    @Size(min = 1, max = 750, message = "Specialties must be 750 characters or fewer")
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
    @Positive(message = "Reservation deadline must be a positive number greater than zero")
    private Integer reservationDeadline;

    @NotNull(message = "Cancellation deadline is mandatory")
    @Positive(message = "Cancellation deadline must be a positive number greater than zero")
    private Integer cancellationDeadline;

    @NotNull(message = "Min duration is mandatory")
    @Min(value = 1, message = "Min duration must be at least 1")
    @Max(value = 24, message = "Min duration cannot exceed 100")
    private Integer minDuration;

    @NotNull(message = "Max duration is mandatory")
    @Min(value = 1, message = "Max duration must be at least 1")
    @Max(value = 24, message = "Max duration cannot exceed 100")
    private Integer maxDuration;

    @NotNull(message = "Availability is mandatory")
    private Boolean isAvailable;

    @NotNull(message = "Visibility is mandatory")
    private Boolean isVisible;
}
