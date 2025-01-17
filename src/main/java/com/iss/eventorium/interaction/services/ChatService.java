package com.iss.eventorium.interaction.services;

import com.iss.eventorium.interaction.dtos.ChatMessageRequestDto;
import com.iss.eventorium.interaction.dtos.ChatMessageResponseDto;
import com.iss.eventorium.interaction.mappers.ChatMapper;
import com.iss.eventorium.interaction.models.ChatMessage;
import com.iss.eventorium.interaction.repositories.ChatMessageRepository;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public void sendMessage(ChatMessageRequestDto chatMessage) {
        if(chatMessage.getMessage().trim().isEmpty()) {
            return;
        }
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
        return Stream.concat(
                        chatMessageRepository.findBySender_IdAndRecipient_Id(senderId, recipientId).stream(),
                        chatMessageRepository.findBySender_IdAndRecipient_Id(recipientId, senderId).stream()
                )
                .map(ChatMapper::toResponse)
                .sorted(Comparator.comparing(ChatMessageResponseDto::getTimestamp))
                .toList();
    }
}
