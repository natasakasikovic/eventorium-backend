package com.iss.eventorium.event.dtos.budget;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetResponseDto {
    private Double plannedAmount;
    private Double spentAmount;
}
