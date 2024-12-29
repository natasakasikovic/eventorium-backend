package com.iss.eventorium.interaction.mappers;

import com.iss.eventorium.interaction.dtos.ChatMessageResponseDto;
import com.iss.eventorium.interaction.models.ChatMessage;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChatMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public ChatMapper(ModelMapper modelMapper) {
        ChatMapper.modelMapper = modelMapper;
    }


    public static ChatMessageResponseDto toResponse(ChatMessage chatMessage) {
        ChatMessageResponseDto dto = modelMapper.map(chatMessage, ChatMessageResponseDto.class);
        dto.setSender(chatMessage.getSender().getPerson().getName());
        dto.setRecipient(chatMessage.getRecipient().getPerson().getName());
        return dto;
    }
}
