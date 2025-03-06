package com.iss.eventorium.interaction.dtos.chat;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomResponseDto {
    private String displayName;
    private LocalDateTime timestamp;
    private String lastMessage;
    private Long recipientId;
}
