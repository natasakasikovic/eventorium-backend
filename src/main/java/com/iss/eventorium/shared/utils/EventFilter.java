package com.iss.eventorium.shared.utils;

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
    private  String eventType;
    private String location;
    private int maxParticipants;
    private LocalDate from;
    private LocalDate to;
}
