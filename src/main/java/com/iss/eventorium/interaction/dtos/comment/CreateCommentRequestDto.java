package com.iss.eventorium.interaction.dtos.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCommentRequestDto {
    @NotBlank
    private String comment;
}
