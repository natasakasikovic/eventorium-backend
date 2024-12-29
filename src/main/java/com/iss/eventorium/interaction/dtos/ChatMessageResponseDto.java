package com.iss.eventorium.interaction.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponseDto {
    private String sender;
    private String recipient;
    private String message;
}
