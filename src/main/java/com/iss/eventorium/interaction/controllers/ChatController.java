package com.iss.eventorium.interaction.controllers;

import com.iss.eventorium.interaction.dtos.ChatMessageResponseDto;
import com.iss.eventorium.interaction.models.ChatMessage;
import com.iss.eventorium.interaction.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat")
    public void sendMessage(@Payload ChatMessage chatMessage){
        chatService.sendMessage(chatMessage);
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessageResponseDto>> getChatMessages(
            @PathVariable("senderId") Long senderId,
            @PathVariable("recipientId") Long recipientId
    ) {
        return ResponseEntity.ok(chatService.getMessages(senderId, recipientId));
    }
}
