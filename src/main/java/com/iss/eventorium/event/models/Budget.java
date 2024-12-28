package com.iss.eventorium.event.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
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
    private Double totalBudget = 0.0;

    @Column(nullable = false)
    @OneToMany(cascade = CascadeType.ALL)
    private List<BudgetItem> items = new ArrayList<>();

    public void addItem(BudgetItem item) {
        this.items.add(item);
        if(item.getPurchased() != null) {
            this.totalBudget += item.getPlannedAmount();
        }
    }
}
