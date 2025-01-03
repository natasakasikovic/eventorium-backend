package com.iss.eventorium.interaction.mappers;

import com.iss.eventorium.interaction.dtos.ChatMessageRequestDto;
import com.iss.eventorium.interaction.dtos.ChatMessageResponseDto;
import com.iss.eventorium.interaction.models.ChatMessage;
import com.iss.eventorium.interaction.dtos.MessageSenderDto;
import com.iss.eventorium.user.models.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
}
