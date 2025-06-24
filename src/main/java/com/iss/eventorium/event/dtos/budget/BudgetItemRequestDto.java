package com.iss.eventorium.event.dtos.budget;

import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.solution.models.SolutionType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetItemRequestDto {
    private Double plannedAmount;
    private Long itemId;
    private CategoryResponseDto category;
    private SolutionType itemType;
}
