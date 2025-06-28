package com.iss.eventorium.event.dtos.budget;

import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.solution.models.SolutionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetItemRequestDto {

    @NotNull(message = "Planned amount is mandatory")
    @Positive(message = "Planned amount must be positive")
    private Double plannedAmount;

    @NotNull(message = "Item id amount is mandatory")
    private Long itemId;

    @NotNull(message = "Category is mandatory")
    private CategoryResponseDto category;

    @NotNull(message = "Item type is mandatory")
    private SolutionType itemType;
}
