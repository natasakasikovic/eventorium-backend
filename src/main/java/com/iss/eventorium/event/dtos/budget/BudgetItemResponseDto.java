package com.iss.eventorium.event.dtos.budget;

import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.event.models.BudgetItemStatus;
import com.iss.eventorium.solution.models.SolutionType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetItemResponseDto {
    private Long id;
    private Double plannedAmount;
    private Double spentAmount;
    private SolutionType type;
    private Long solutionId;
    private String solutionName;
    private BudgetItemStatus status;
    private CategoryResponseDto category;
}
