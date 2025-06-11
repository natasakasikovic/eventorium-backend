package com.iss.eventorium.notifications.mappers;

import com.iss.eventorium.notifications.dtos.NotificationResponseDto;
import com.iss.eventorium.notifications.models.Notification;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationMapper {

    private final ModelMapper modelMapper;

    public NotificationResponseDto toResponse(Notification notification) {
        return modelMapper.map(notification, NotificationResponseDto.class);
    }
}
