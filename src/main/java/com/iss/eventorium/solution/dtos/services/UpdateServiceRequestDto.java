package com.iss.eventorium.solution.dtos.services;

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
public class UpdateServiceRequestDto {

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotBlank(message = "Specialties are mandatory")
    private String specialties;

    @NotBlank(message = "Price is mandatory")
    private Double price;

    @NotBlank(message = "Discount is mandatory")
    private Double discount;

    @NotBlank(message = "Event types IDs are mandatory")
    private List<Long> eventTypesIds;

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

    @NotBlank(message = "Availability is mandatory")
    private Boolean available;

    @NotBlank(message = "Visibility is mandatory")
    private Boolean visible;
}