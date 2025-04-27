package com.iss.eventorium.interaction.mappers;

import com.iss.eventorium.interaction.dtos.chat.ChatMessageRequestDto;
import com.iss.eventorium.interaction.dtos.chat.ChatMessageResponseDto;
import com.iss.eventorium.interaction.dtos.chat.ChatRoomResponseDto;
import com.iss.eventorium.interaction.models.ChatMessage;
import com.iss.eventorium.interaction.dtos.chat.MessageSenderDto;
import com.iss.eventorium.interaction.models.ChatRoom;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.user.models.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ChatMapper {
    private final ModelMapper modelMapper;

    public ChatMessageResponseDto toResponse(ChatMessage chatMessage) {
        ChatMessageResponseDto dto = modelMapper.map(chatMessage, ChatMessageResponseDto.class);
        dto.setSenderId(chatMessage.getSender().getId());
        dto.setRecipientId(chatMessage.getRecipient().getId());
        dto.setSender(
                MessageSenderDto.builder()
                        .id(chatMessage.getSender().getId())
                        .name(chatMessage.getSender().getPerson().getName())
                        .lastname(chatMessage.getSender().getPerson().getLastname())
                        .build()
        );
        return dto;
    }

    public ChatMessage fromRequest(ChatMessageRequestDto dto, User sender, User recipient) {
        return ChatMessage.builder()
                .timestamp(LocalDateTime.now())
                .sender(sender)
                .recipient(recipient)
                .message(dto.getMessage())
                .build();
    }

    public ChatRoomResponseDto toResponse(ChatRoom chatRoom, User currentUser) {
        User user = chatRoom.getLastMessage().getSender().equals(currentUser)
                ? chatRoom.getLastMessage().getRecipient()
                : chatRoom.getLastMessage().getSender();

        ChatMessage lastMessage = chatRoom.getLastMessage();
        return ChatRoomResponseDto.builder()
                .id(chatRoom.getId())
                .displayName(user.getPerson().getName() + " " + user.getPerson().getLastname())
                .lastMessage(lastMessage.getMessage())
                .recipientId(user.getId())
                .timestamp(lastMessage.getTimestamp())
                .build();
    }

    public PagedResponse<ChatRoomResponseDto> toPagedResponse(Page<ChatRoom> page, User currentUser) {
        return new PagedResponse<>(
                page.stream().map(room -> toResponse(room, currentUser)).toList(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

}
