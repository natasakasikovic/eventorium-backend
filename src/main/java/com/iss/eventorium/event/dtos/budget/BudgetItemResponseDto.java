package com.iss.eventorium.event.dtos.budget;

import com.iss.eventorium.category.dtos.CategoryResponseDto;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetItemResponseDto {
    private Double plannedAmount;
    private Double spentAmount;
    private LocalDateTime purchased;
    private CategoryResponseDto category;
}
