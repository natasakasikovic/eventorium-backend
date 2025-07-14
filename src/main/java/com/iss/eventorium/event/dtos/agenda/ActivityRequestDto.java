package com.iss.eventorium.event.dtos.agenda;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityRequestDto {

    @NotBlank(message = "Activity name is required")
    @Size(max = 50, message = "Activity name must not exceed 50 characters")
    private String name;

    @NotBlank(message = "Event description is required")
    @Size(max = 200, message = "Event description must not exceed 200 characters")
    private String description;

    @NotNull(message = "Start time is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "h:mm a")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "h:mm a")
    private LocalTime endTime;

    @NotBlank(message = "Location is required")
    @Size(max = 50, message = "Location must not exceed 50 characters")
    private String location;
}
