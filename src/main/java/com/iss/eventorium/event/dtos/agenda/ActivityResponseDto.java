package com.iss.eventorium.event.dtos.agenda;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class ActivityResponseDto {
    private Long id;
    private String name;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "h:mm a")
    private LocalTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "h:mm a")
    private LocalTime endTime;
    private String location;
}
