package com.iss.eventorium.interaction.dtos.comment;

import com.iss.eventorium.shared.models.Status;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCommentRequestDto {
    @NotNull
    private Status status;
}
