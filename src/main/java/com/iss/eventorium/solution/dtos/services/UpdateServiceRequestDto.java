package com.iss.eventorium.solution.dtos.services;

import com.iss.eventorium.solution.models.ReservationType;
import com.iss.eventorium.solution.validators.DurationConstraint;
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
public class UpdateServiceRequestDto {

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

    @NotEmpty(message = "Event types IDs are mandatory")
    private List<Long> eventTypesIds;

    @NotNull(message = "Reservation type is mandatory")
    private ReservationType type;

    @NotNull(message = "Reservation deadline is mandatory")
    @FutureOrPresent(message = "Reservation deadline cannot be in the past")
    private LocalDate reservationDeadline;

    @NotNull(message = "Cancellation deadline is mandatory")
    @FutureOrPresent(message = "Cancellation deadline cannot be in the past")
    private LocalDate cancellationDeadline;

    @NotNull(message = "Min duration is mandatory")
    @Min(value = 1, message = "Min duration must be at least 1")
    @Max(value = 24, message = "Min duration cannot exceed 100")
    private Integer minDuration;

    @NotNull(message = "Max duration is mandatory")
    @Min(value = 1, message = "Max duration must be at least 1")
    @Max(value = 24, message = "Max duration cannot exceed 100")
    private Integer maxDuration;

    @NotNull(message = "Availability is mandatory")
    private Boolean available;

    @NotNull(message = "Visibility is mandatory")
    private Boolean visible;
}
