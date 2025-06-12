package com.iss.eventorium.event.models;

import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.solution.models.Solution;
import com.iss.eventorium.solution.models.SolutionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "budget_items")
public class BudgetItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "planned_amount")
    private Double plannedAmount;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Solution solution;

    @Enumerated(EnumType.STRING)
    private SolutionType itemType;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Category category;

    private LocalDateTime processedAt;
}
