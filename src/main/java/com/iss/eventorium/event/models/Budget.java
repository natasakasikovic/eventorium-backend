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

    @Column(nullable = false, name="planned_amount")
    private double plannedAmount;

    @Column(nullable = false, name = "spent_amount")
    private Double spentAmount = 0.0;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<BudgetItem> items = new ArrayList<>();

    public void addItem(BudgetItem item) {
        this.items.add(item);
        if(item.getProcessedAt() != null) {
            this.spentAmount += item.getSolution().getPrice() * (1 - item.getSolution().getDiscount() / 100);
        }
        this.plannedAmount += item.getPlannedAmount();
    }
}
