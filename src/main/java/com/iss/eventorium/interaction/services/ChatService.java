package com.iss.eventorium.interaction.services;

import com.iss.eventorium.interaction.dtos.chat.ChatMessageRequestDto;
import com.iss.eventorium.interaction.dtos.chat.ChatMessageResponseDto;
import com.iss.eventorium.interaction.mappers.ChatMapper;
import com.iss.eventorium.interaction.models.ChatMessage;
import com.iss.eventorium.interaction.repositories.ChatMessageRepository;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.repositories.UserRepository;
import com.iss.eventorium.user.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final UserService userService;
    private final ChatRoomService chatRoomService;

    private final ChatMessageRepository chatMessageRepository;

    private final ChatMapper mapper;

    private final SimpMessagingTemplate messagingTemplate;

    public void sendMessage(ChatMessageRequestDto chatMessage) {
        if(chatMessage.getMessage().trim().isEmpty()) {
            return;
        }

        User sender = userService.find(chatMessage.getSenderId());
        User recipient = userService.find(chatMessage.getRecipientId());

        log.info("Sending message from {} to {}: {}",
            chatMessage.getSenderId(),
            chatMessage.getRecipientId(),
            chatMessage.getMessage()
        );

        ChatMessage message = chatMessageRepository.save(mapper.fromRequest(chatMessage, sender, recipient));
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId().toString(),
                "/queue/messages",
                mapper.toResponse(message)
        );
        chatRoomService.createChatRoom(sender, recipient, message);
    }

    public List<ChatMessageResponseDto> getMessages(Long senderId, Long recipientId) {
        return Stream.concat(
                        chatMessageRepository.findBySender_IdAndRecipient_Id(senderId, recipientId).stream(),
                        chatMessageRepository.findBySender_IdAndRecipient_Id(recipientId, senderId).stream()
                )
                .map(mapper::toResponse)
                .sorted(Comparator.comparing(ChatMessageResponseDto::getTimestamp))
                .toList();
    }
}
