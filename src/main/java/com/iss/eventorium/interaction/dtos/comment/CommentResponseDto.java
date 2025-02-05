package com.iss.eventorium.interaction.dtos.comment;

import com.iss.eventorium.interaction.models.CommentType;
import com.iss.eventorium.shared.dtos.CommentableResponseDto;
import com.iss.eventorium.shared.models.CommentableEntity;
import com.iss.eventorium.user.dtos.user.UserDetailsDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDto {
    private Long id;
    private LocalDateTime creationDate;
    private UserDetailsDto user;
    private CommentType type;
    private CommentableResponseDto commentable;
}
