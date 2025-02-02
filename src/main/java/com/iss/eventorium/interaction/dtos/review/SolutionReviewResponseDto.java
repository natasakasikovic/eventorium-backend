package com.iss.eventorium.interaction.dtos.review;

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
