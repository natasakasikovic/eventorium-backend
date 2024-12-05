package com.iss.eventorium.solution.models;

import com.iss.eventorium.event.models.EventType;
import com.iss.eventorium.shared.models.Status;
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
