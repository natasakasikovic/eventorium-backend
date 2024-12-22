package com.iss.eventorium.interaction.services;

import com.iss.eventorium.interaction.models.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(Long userId, Notification notification) {
        log.info("Sending WebSocket notification to {} with payload {}", userId, notification);
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/notifications",
                notification
        );
    }
}
