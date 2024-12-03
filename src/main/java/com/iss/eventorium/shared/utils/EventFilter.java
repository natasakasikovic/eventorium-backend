package com.iss.eventorium.shared.utils;

import com.iss.eventorium.event.models.Privacy;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFilter {
    private String name;
    private String description;
    private String eventType;
    private String location;
    private Integer maxParticipants;
    private Privacy privacy;
    private LocalDate from;
    private LocalDate to;
}
