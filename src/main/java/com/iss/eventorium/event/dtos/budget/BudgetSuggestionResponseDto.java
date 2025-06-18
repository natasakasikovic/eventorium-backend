package com.iss.eventorium.event.dtos.budget;

import com.iss.eventorium.solution.models.SolutionType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetSuggestionResponseDto {
    private Long id;
    private SolutionType solutionType;
    private String name;
    private Double price;
    private Double discount;
    private Double rating;
}
