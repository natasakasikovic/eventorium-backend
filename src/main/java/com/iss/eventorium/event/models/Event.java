package com.iss.eventorium.event.models;

import com.iss.eventorium.shared.models.City;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "events")
@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private String name;

    @Column (nullable = false)
    private String description;

    @Column (nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private Privacy privacy;

    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants;

    @ManyToOne
    private EventType type;

    @ManyToOne
    private City city;

    @Column
    private String address;

    @OneToMany
    private List<Activity> activities;

//    private Budget budget;
// NOTE: add missing attributes as classes are implemented!
}
