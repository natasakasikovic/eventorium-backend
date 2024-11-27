package com.iss.eventorium.interaction.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRatingRequestDto {
    private String feedback;
    private Integer rating;
}
