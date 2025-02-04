package com.iss.eventorium.interaction.dtos.ratings;

import com.iss.eventorium.user.dtos.user.UserDetailsDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingResponseDto {
    private Long id;
    private LocalDateTime creationDate;
    private Integer rating;
    private UserDetailsDto user;
}
