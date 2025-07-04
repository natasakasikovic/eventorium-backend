package com.iss.eventorium.event.dtos.budget;

import com.iss.eventorium.category.dtos.CategoryResponseDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetResponseDto {
    private Double plannedAmount;
    private Double spentAmount;
    private List<CategoryResponseDto> activeCategories;
}
