package com.iss.eventorium.event.models;

import com.iss.eventorium.category.models.Category;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "event_types")
@Builder
@Entity
public class EventType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private boolean deleted;

    @ManyToMany
    private List<Category> suggestedCategories;

    @Override
    public String toString() {
        return name;
    }
}
