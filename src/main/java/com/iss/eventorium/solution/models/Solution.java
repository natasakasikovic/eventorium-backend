package com.iss.eventorium.solution.models;

import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.event.models.EventType;
import com.iss.eventorium.interaction.models.Review;
import com.iss.eventorium.shared.models.ImagePath;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.user.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@SQLRestriction("is_deleted = false")
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

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn (name = "solution_id")
    private List<Review> reviews;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="category_id")
    private Category category;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="solution_event_types", joinColumns = @JoinColumn(name = "solution_id"), inverseJoinColumns = @JoinColumn(name = "event_type_id"))
    private List<EventType> eventTypes;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ImagePath> imagePaths;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "provider_id")
    private User provider;

    public abstract void restore(Memento memento);

    public void addReview(Review review) {
        getReviews().add(review);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Solution solution)) return false;
        return Objects.equals(id, solution.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
