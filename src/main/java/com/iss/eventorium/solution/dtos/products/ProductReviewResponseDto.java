package com.iss.eventorium.solution.dtos.products;

import com.iss.eventorium.interaction.dtos.ratings.RatingResponseDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReviewResponseDto {
    private Long id;
    private String name;
    private Double price;
    private Double discount;
    private List<RatingResponseDto> reviews;
}
