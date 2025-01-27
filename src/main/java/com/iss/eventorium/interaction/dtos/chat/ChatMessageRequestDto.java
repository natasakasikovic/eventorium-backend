package com.iss.eventorium.interaction.dtos.chat;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageRequestDto {
    private Long senderId;
    private Long recipientId;
    private String message;
}
