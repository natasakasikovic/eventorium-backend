package com.iss.eventorium.event.dtos;

import com.iss.eventorium.event.models.Privacy;
import com.iss.eventorium.shared.models.City;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDto {


    @NotNull(message = "Event name is required")
    @NotBlank(message = "Event name cannot be blank")
    private String name;

    @NotNull(message = "Event description is required")
    @NotBlank(message = "Event description cannot be blank")
    private String description;

    @NotNull(message = "Event date is required")
    @FutureOrPresent(message = "Event date must not be in the past")
    private LocalDate date;

    @NotNull(message = "Privacy field is required")
    private Privacy privacy;

    @NotNull(message = "Max participants field is required")
    @Positive(message = "Max participants must be a positive number greater than zero")
    private Integer maxParticipants;

    private EventTypeResponseDto eventType = null;

    @NotNull(message = "City field is required")
    private City city;

    @NotNull(message = "Address field is required")
    @NotBlank(message = "Address field cannot be blank")
    private String address;
}
