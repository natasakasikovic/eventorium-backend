package com.iss.eventorium.interaction.mappers;

import com.iss.eventorium.interaction.dtos.chat.ChatMessageRequestDto;
import com.iss.eventorium.interaction.dtos.chat.ChatMessageResponseDto;
import com.iss.eventorium.interaction.dtos.chat.ChatRoomResponseDto;
import com.iss.eventorium.interaction.models.ChatMessage;
import com.iss.eventorium.interaction.dtos.chat.MessageSenderDto;
import com.iss.eventorium.interaction.models.ChatRoom;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.solution.mappers.ServiceMapper;
import com.iss.eventorium.user.models.Person;
import com.iss.eventorium.user.models.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ChatMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public ChatMapper(ModelMapper modelMapper) {
        ChatMapper.modelMapper = modelMapper;
    }

    public static ChatMessageResponseDto toResponse(ChatMessage chatMessage) {
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

    public static ChatMessage fromRequest(ChatMessageRequestDto dto, User sender, User recipient) {
        return ChatMessage.builder()
                .timestamp(LocalDateTime.now())
                .sender(sender)
                .recipient(recipient)
                .message(dto.getMessage())
                .build();
    }

    public static ChatRoomResponseDto toResponse(ChatRoom chatRoom) {
        Person recipient = chatRoom.getLastMessage().getRecipient().getPerson();
        ChatMessage lastMessage = chatRoom.getLastMessage();
        return ChatRoomResponseDto.builder()
                .id(chatRoom.getId())
                .displayName(recipient.getName() + " " + recipient.getLastname())
                .lastMessage(lastMessage.getMessage())
                .recipientId(lastMessage.getRecipient().getId())
                .timestamp(lastMessage.getTimestamp())
                .build();
    }

    public static PagedResponse<ChatRoomResponseDto> toPagedResponse(Page<ChatRoom> page) {
        return new PagedResponse<>(
                page.stream().map(ChatMapper::toResponse).toList(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

}
