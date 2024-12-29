package com.iss.eventorium.interaction.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatNotification {
    private Long id;
    private Long senderId;
    private Long recipientId;
    private String content;
}
