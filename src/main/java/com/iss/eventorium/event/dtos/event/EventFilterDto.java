package com.iss.eventorium.event.dtos.event;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFilterDto {
    private String name;
    private String description;
    private String eventType;
    private String city;

    @Positive(message = "The value of maximum participans must be greater than zero!")
    private Integer maxParticipants;

    @FutureOrPresent(message = "Event date must not be in the past!")
    private LocalDate from;

    @FutureOrPresent(message = "Event date must not be in the past!")
    private LocalDate to;
}
