package com.iss.eventorium.event.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "total_budget")
    private Double totalBudget;

    @Column(nullable = false)
    @OneToMany
    private List<BudgetItem> items;
}
