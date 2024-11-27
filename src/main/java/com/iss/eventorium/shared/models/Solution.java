package com.iss.eventorium.shared.models;

import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.interaction.models.Rating;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Solution {
    private Long id;
    private String name;
    private String description;
    private String specialties;
    private double price;
    private double discount;
    private Status status;
    private LocalDateTime validFrom;
    private boolean isAvailable;
    private boolean isDeleted;
    private boolean isVisible;
    private List<EventType> eventTypes;
    private Category category;
    private List<Rating> ratings;

    public void restore(SolutionMemento memento) {
        this.name = memento.name();
        this.price = memento.price();
        this.discount = memento.discount();
        this.isAvailable = memento.isAvailable();
        this.isDeleted = memento.isDeleted();
        this.eventTypes = memento.eventTypes();
    }
}
