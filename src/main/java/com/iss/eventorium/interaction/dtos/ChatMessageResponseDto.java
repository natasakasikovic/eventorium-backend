package com.iss.eventorium.interaction.dtos;

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
}
