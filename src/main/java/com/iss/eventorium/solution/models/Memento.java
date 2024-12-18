package com.iss.eventorium.solution.models;

import com.iss.eventorium.event.models.EventType;
import com.iss.eventorium.shared.models.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mementos")
public class Memento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private double price;
    private double discount;
    private Status status;
    private boolean isAvailable;
    private boolean isVisible;

    @ManyToMany
    private List<EventType> eventTypes;

    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    @Enumerated(EnumType.STRING)
    private ReservationType type;
    private LocalDate reservationDeadline;
    private LocalDate cancellationDeadline;
    private Integer minDuration;
    private Integer maxDuration;
}
