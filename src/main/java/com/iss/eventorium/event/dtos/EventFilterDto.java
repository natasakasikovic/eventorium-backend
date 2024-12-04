package com.iss.eventorium.event.dtos;

import com.iss.eventorium.event.models.Privacy;
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
    private String location;
    private Integer maxParticipants;
    private Privacy privacy;
    private LocalDate from;
    private LocalDate to;
}
