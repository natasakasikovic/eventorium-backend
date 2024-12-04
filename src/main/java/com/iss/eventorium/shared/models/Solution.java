package com.iss.eventorium.shared.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Solution {

    @Id
    @SequenceGenerator(name = "solutionSeqGen", sequenceName = "solutionSequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "solutionSeqGen")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String specialties;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Double discount;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name="valid_from")
    private LocalDateTime validFrom;

    @Column(name="is_available")
    private Boolean isAvailable;

    @Column(name="is_deleted")
    private Boolean isDeleted;

    @Column(name="is_visible")
    private Boolean isVisible;

//    private List<EventType> eventTypes;
//    private Category category;
//    private List<Review> reviews;

    public void restore(SolutionMemento memento) {
        this.name = memento.name();
        this.price = memento.price();
        this.discount = memento.discount();
        this.isAvailable = memento.isAvailable();
        this.isDeleted = memento.isDeleted();
//        this.eventTypes = memento.eventTypes();
    }
}
