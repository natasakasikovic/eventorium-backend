package com.iss.eventorium.interaction.dtos.comment;

import com.iss.eventorium.user.dtos.user.UserDetailsDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManageCommentResponseDto {
    private Long id;
    private LocalDateTime creationDate;
    private String feedback;
    private UserDetailsDto user;
}
