package com.iss.eventorium.interaction.dtos.comment;

import com.iss.eventorium.interaction.models.CommentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCommentRequestDto {

    @NotBlank(message = "Comment is mandatory")
    @Size(min = 1, max = 100, message = "Comment must be 100 characters or fewer")
    private String comment;

    @NotNull(message = "Type is mandatory")
    private CommentType type;

    @NotNull(message = "Object id is mandatory")
    private Long objectId;
}
