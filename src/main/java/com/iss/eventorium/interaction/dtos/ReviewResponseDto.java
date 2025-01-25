package com.iss.eventorium.interaction.dtos;

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
    private String user;
}
