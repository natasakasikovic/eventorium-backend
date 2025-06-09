package com.iss.eventorium.interaction.dtos.comment;

import com.iss.eventorium.interaction.models.CommentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCommentRequestDto {

    @NotBlank(message = "Comment is mandatory")
    private String comment;

    @NotNull(message = "Type is mandatory")
    private CommentType type;

    @NotNull(message = "Object id is mandatory")
    private Long objectId;
}
