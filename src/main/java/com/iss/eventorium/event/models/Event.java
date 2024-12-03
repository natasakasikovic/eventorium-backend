package com.iss.eventorium.event.models;

import com.iss.eventorium.shared.models.Location;
import jakarta.persistence.*;
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
    private Date date;

    @Enumerated(EnumType.STRING)
    private Privacy privacy;

    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants;

    @Embedded
    private Location location;

//    private Budget budget;
//    private EventType type;
// NOTE: add missing attributes as classes are implemented!
}
