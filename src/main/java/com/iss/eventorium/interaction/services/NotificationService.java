package com.iss.eventorium.interaction.services;

import com.iss.eventorium.interaction.repositories.NotificationRepository;
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
        log.info("Sending notification to {}: {}", userId, notification.getMessage());
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/notifications",
                toResponse(notification)
        );
        notificationRepository.save(notification);
    }

    public void sendNotificationToAdmin(Notification notification) {
        log.info("Sending notification to admins: {}", notification.getMessage());
        messagingTemplate.convertAndSend(
          "/topic/admin",
          toResponse(notification)
        );
        notificationRepository.save(notification);
    }
}
