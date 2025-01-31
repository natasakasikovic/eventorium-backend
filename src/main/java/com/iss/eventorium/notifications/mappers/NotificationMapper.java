package com.iss.eventorium.notifications.mappers;

import com.iss.eventorium.notifications.dtos.NotificationResponseDto;
import com.iss.eventorium.notifications.models.Notification;
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
