package com.iss.eventorium.event.models;

import com.iss.eventorium.interaction.models.Rating;
import com.iss.eventorium.shared.models.City;
import com.iss.eventorium.shared.models.CommentableEntity;
import com.iss.eventorium.user.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
@Entity
public class Event extends CommentableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private Privacy privacy;

    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants;

    // NOTE: If type is null, it indicates that the user selected "all"
    @ManyToOne
    private EventType type;

    @ManyToOne
    private City city;

    @Column
    private String address;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "event_id")
    private List<Activity> activities = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "event_id")
    private List<Rating> ratings;

    @ManyToOne
    private User organizer;

    @OneToOne(cascade = CascadeType.ALL)
    private Budget budget;

    @Column(name = "is_draft")
    private boolean isDraft = true;

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public User getCreator() {
        return organizer;
    }
}

