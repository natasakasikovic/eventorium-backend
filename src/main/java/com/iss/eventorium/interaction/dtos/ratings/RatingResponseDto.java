package com.iss.eventorium.interaction.dtos.ratings;

import com.iss.eventorium.user.dtos.user.UserDetailsDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingResponseDto {
    private Long id;
    private Integer rating;
    private UserDetailsDto user;
}
