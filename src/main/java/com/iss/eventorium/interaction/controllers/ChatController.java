package com.iss.eventorium.interaction.controllers;

import com.iss.eventorium.interaction.dtos.ChatMessageRequestDto;
import com.iss.eventorium.interaction.dtos.ChatMessageResponseDto;
import com.iss.eventorium.interaction.services.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat")
    public void sendMessage(@Payload @Valid ChatMessageRequestDto chatMessage){
        chatService.sendMessage(chatMessage);
    }

    @GetMapping("/messages/{sender-id}/{recipient-id}")
    public ResponseEntity<List<ChatMessageResponseDto>> getChatMessages(
            @PathVariable("sender-id") Long senderId,
            @PathVariable("recipient-id") Long recipientId
    ) {
        return ResponseEntity.ok(chatService.getMessages(senderId, recipientId));
    }
}
