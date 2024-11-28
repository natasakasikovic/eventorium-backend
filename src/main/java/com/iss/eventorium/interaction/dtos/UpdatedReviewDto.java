package com.iss.eventorium.interaction.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatedReviewDto {
    private Long id;
    private int rating;
    private String feedback;
}
