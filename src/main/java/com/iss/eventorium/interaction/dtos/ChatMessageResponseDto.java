package com.iss.eventorium.interaction.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponseDto {
    private Long senderId;
    private Long recipientId;
    private String message;
}
