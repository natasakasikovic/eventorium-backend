package com.iss.eventorium.solution.models;

import com.iss.eventorium.event.models.EventType;
import com.iss.eventorium.shared.models.Status;
import jakarta.annotation.Nonnull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceMemento {
    private String name;
    private double price;
    private double discount;
    private Status status;
    private boolean isAvailable;
    private boolean isDeleted;
    private List<EventType> eventTypes;
    private LocalDateTime validFrom;
}
