package com.iss.eventorium.shared.models;

import com.iss.eventorium.event.models.EventType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record SolutionMemento(
        String name,
        double price,
        double discount,
        Status status,
        boolean isAvailable,
        boolean isDeleted,
        List<EventType> eventTypes,
        LocalDateTime validFrom
) {
}
