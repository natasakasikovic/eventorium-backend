package com.iss.eventorium.solution.dtos.products;

import com.iss.eventorium.interaction.dtos.ratings.RatingResponseDto;
import com.iss.eventorium.solution.models.SolutionType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolutionReviewResponseDto {
    private Long id;
    private String name;
    private Double price;
    private Double discount;
    private RatingResponseDto rating;
    private SolutionType type;
}
