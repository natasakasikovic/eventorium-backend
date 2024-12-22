package com.iss.eventorium.interaction.mappers;

import com.iss.eventorium.interaction.dtos.NotificationResponseDto;
import com.iss.eventorium.interaction.models.Notification;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public NotificationMapper(ModelMapper modelMapper) {
        NotificationMapper.modelMapper = modelMapper;
    }

    public static NotificationResponseDto toResponse(Notification notification) {
        return modelMapper.map(notification, NotificationResponseDto.class);
    }

}
