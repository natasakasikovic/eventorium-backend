package com.iss.eventorium.solution.dtos.services;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRequestDto {

    @NotNull(message = "Starting time is required!")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "h:mm a")
    private LocalTime startingTime;

    @NotNull(message = "Ending time is required!")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "h:mm a")
    private LocalTime endingTime;

    @NotNull(message = "Planned amount is mandatory!")
    @Positive(message = "Planned amount must be greater than zero")
    private Double plannedAmount;
}