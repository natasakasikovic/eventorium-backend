package com.iss.eventorium.interaction.dtos.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCommentRequestDto {

    @NotBlank(message = "Comment is mandatory")
    private String comment;
}
