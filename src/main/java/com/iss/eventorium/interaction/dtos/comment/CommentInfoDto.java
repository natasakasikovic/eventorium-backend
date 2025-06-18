package com.iss.eventorium.interaction.dtos.comment;

import com.iss.eventorium.user.models.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentInfoDto {
    private String displayName;
    private User user;
}
