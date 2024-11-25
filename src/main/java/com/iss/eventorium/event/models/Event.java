package com.iss.eventorium.event.models;

import com.iss.eventorium.shared.models.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private Long id;
    private String name;
    private String description;
    private int maxParticipants;
    private Date date;
    private Privacy privacy;
    private EventType type;
    private Location location;
    // NOTE: add missing attributes as classes are implemented!
}
