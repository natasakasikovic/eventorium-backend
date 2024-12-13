package com.iss.eventorium.event.dtos;

import com.iss.eventorium.category.dtos.CategoryRequestDto;
import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.solution.models.Solution;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetItemRequestDto {
    private Double plannedAmount;
    private Long solutionId;
    private CategoryResponseDto category;
}
