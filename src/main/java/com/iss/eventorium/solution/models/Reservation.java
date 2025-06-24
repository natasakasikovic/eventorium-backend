package com.iss.eventorium.solution.models;

import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.shared.models.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "service_reservations")
@SQLRestriction("is_canceled = false")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Event event;

    @ManyToOne
    private Service service;

    @Column(name="starting_time", nullable = false)
    private LocalTime startingTime;

    @Column(name="ending_time", nullable = false)
    private LocalTime endingTime;

    @Column(name="is_canceled", nullable = false)
    private Boolean isCanceled = false;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;
}
