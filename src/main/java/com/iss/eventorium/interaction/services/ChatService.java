package com.iss.eventorium.interaction.services;

import com.iss.eventorium.interaction.dtos.ChatMessageRequestDto;
import com.iss.eventorium.interaction.dtos.ChatMessageResponseDto;
import com.iss.eventorium.interaction.mappers.ChatMapper;
import com.iss.eventorium.interaction.models.ChatMessage;
import com.iss.eventorium.interaction.models.ChatNotification;
import com.iss.eventorium.interaction.models.ChatRoom;
import com.iss.eventorium.interaction.repositories.ChatMessageRepository;
import com.iss.eventorium.interaction.repositories.ChatRoomRepository;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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
    private final UserRepository userRepository;

    public void sendMessage(ChatMessageRequestDto chatMessage) {
        log.info("Sending message from {} to {}: {}",
            chatMessage.getSenderId(),
            chatMessage.getRecipientId(),
            chatMessage.getMessage()
        );
        User sender = userRepository.findById(chatMessage.getSenderId()).orElseThrow(
                () -> new EntityNotFoundException("Sender not found")
        );
        User recipient = userRepository.findById(chatMessage.getRecipientId()).orElseThrow(
                () -> new EntityNotFoundException("Recipient not found")
        );

        ChatMessage message = chatMessageRepository.save(ChatMapper.fromRequest(chatMessage, sender, recipient));
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId().toString(),
                "/queue/messages",
                ChatMapper.toResponse(message)
        );
    }

    public List<ChatMessageResponseDto> getMessages(Long senderId, Long recipientId) {
        ChatRoom chatRoom = chatRoomRepository.findBySender_IdAndRecipient_Id(senderId, recipientId).orElse(null);
        if(chatRoom == null) {
            return new ArrayList<>();
        }
        return chatMessageRepository.findByChatName(chatRoom.getName()).stream().map(ChatMapper::toResponse).toList();
    }
}
