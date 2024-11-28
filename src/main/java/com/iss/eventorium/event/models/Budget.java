package com.iss.eventorium.event.models;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {
    private Long id;
    private Double totalBudget;
    private List<BudgetItem> items;
}
