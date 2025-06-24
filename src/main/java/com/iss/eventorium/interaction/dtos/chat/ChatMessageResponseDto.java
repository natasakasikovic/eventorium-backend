package com.iss.eventorium.interaction.dtos.chat;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponseDto {
    private Long senderId;
    private Long recipientId;
    private String message;
    private LocalDateTime timestamp;
    private MessageSenderDto sender;
}
