package com.iss.eventorium.solution.models;

import com.iss.eventorium.event.models.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.sql.Time;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    private Long id;
    private Event event;
    private Service service;
    private Time from;
    private Time to;
}
