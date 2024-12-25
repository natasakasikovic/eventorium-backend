package com.iss.eventorium.interaction.services;

import com.iss.eventorium.category.repositories.NotificationRepository;
import com.iss.eventorium.interaction.models.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


import static com.iss.eventorium.interaction.mappers.NotificationMapper.toResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    private final NotificationRepository notificationRepository;

    public void sendNotification(Long userId, Notification notification) {
        log.info("Sending WebSocket notification to {} with payload {}", userId, notification);
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/notifications",
                toResponse(notification)
        );
        notificationRepository.save(notification);
    }
}
