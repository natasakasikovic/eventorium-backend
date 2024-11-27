package com.iss.eventorium.interaction.dtos;

import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.users.models.Person;
import lombok.*;
import org.springframework.context.annotation.Bean;

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
    private String feedback;
    private Status status;
}
