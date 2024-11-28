package com.iss.eventorium.interaction.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateReviewDto {
    private int rating;
    private String feedback;
}
