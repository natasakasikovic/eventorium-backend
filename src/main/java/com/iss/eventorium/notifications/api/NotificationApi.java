package com.iss.eventorium.notifications.api;

import com.iss.eventorium.notifications.dtos.NotificationResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface NotificationApi {

    ResponseEntity<List<NotificationResponseDto>> getNotifications();
    ResponseEntity<Void> markNotificationsAsSeen();

}
