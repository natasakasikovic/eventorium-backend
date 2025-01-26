package com.iss.eventorium.interaction.dtos.review;

import com.iss.eventorium.user.dtos.user.UserDetailsDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDto {
    private Long id;
    private LocalDateTime creationDate;
    private Integer rating;
    private String feedback;
    private UserDetailsDto user;
}
