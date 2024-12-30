package com.iss.eventorium.interaction.dtos;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageRequestDto {
    private String chatName;
    private Long senderId;
    private Long recipientId;
    private String message;
}
