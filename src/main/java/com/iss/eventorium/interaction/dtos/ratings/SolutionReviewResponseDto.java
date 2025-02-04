package com.iss.eventorium.interaction.dtos.ratings;

import com.iss.eventorium.solution.models.SolutionType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolutionReviewResponseDto {
    private Long id;
    private String name;
    private SolutionType solutionType;
}
