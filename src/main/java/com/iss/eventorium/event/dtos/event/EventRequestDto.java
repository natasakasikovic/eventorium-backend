package com.iss.eventorium.event.dtos.event;

import com.iss.eventorium.event.dtos.eventtype.EventTypeResponseDto;
import com.iss.eventorium.event.models.Privacy;
import com.iss.eventorium.shared.dtos.CityDto;
import jakarta.validation.constraints.*;
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

    @NotBlank(message = "Event name is required")
    @Size(max = 50, message = "Event name must not exceed 50 characters")
    private String name;

    @NotBlank(message = "Event description is required")
    @Size(max = 200, message = "Event description must not exceed 200 characters")
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
    private CityDto city;

    @NotBlank(message = "Address field is required")
    @Size(max = 50, message = "Address must not exceed 50 characters")
    private String address;
}
