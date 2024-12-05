package com.iss.eventorium.event.models;

import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.solution.models.Solution;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetItem {
    private Long id;
    private Double plannedAmount;
    private Solution solution;
    private Category category;
    private LocalDateTime purchased;
}
