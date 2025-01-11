package com.iss.eventorium.interaction.dtos;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageRequestDto {
    private Long senderId;
    private Long recipientId;
    @NotBlank
    private String message;
}
