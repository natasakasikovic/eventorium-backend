package com.iss.eventorium.interaction.services;

import com.iss.eventorium.interaction.dtos.ChatMessageResponseDto;
import com.iss.eventorium.interaction.mappers.ChatMapper;
import com.iss.eventorium.interaction.models.ChatMessage;
import com.iss.eventorium.interaction.models.ChatNotification;
import com.iss.eventorium.interaction.models.ChatRoom;
import com.iss.eventorium.interaction.repositories.ChatMessageRepository;
import com.iss.eventorium.interaction.repositories.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    public void sendMessage(ChatMessage chatMessage) {
        log.info("Sending message from {} to {}: {}",
            chatMessage.getSender().getId(),
            chatMessage.getRecipient().getId(),
            chatMessage.getMessage()
        );
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipient().getId().toString(),
                "/queue/messages",
                new ChatNotification(
                        chatMessage.getId(),
                        chatMessage.getSender().getId(),
                        chatMessage.getRecipient().getId(),
                        chatMessage.getMessage()
                )
        );
        chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessageResponseDto> getMessages(Long senderId, Long recipientId) {
        ChatRoom chatRoom = chatRoomRepository.findBySender_IdAndRecipient_Id(senderId, recipientId).orElse(null);
        if(chatRoom == null) {
            return new ArrayList<>();
        }
        return chatMessageRepository.findByChatName(chatRoom.getName()).stream().map(ChatMapper::toResponse).toList();
    }
}
