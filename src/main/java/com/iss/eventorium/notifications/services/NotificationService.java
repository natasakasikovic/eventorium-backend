package com.iss.eventorium.notifications.services;

import com.iss.eventorium.notifications.repositories.NotificationRepository;
import com.iss.eventorium.notifications.dtos.NotificationResponseDto;
import com.iss.eventorium.notifications.mappers.NotificationMapper;
import com.iss.eventorium.notifications.models.Notification;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.services.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


import java.util.List;

import static com.iss.eventorium.notifications.mappers.NotificationMapper.toResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final AuthService authService;
    private final NotificationRepository repository;

    public void sendNotification(User user, Notification notification) {
        log.info("Sending notification to {}: {}", user, notification.getMessage());
        messagingTemplate.convertAndSendToUser(
                user.getId().toString(),
                "/notifications",
                toResponse(notification)
        );
        notification.setRecipient(user);
        repository.save(notification);
    }

    public void sendNotificationToAdmin(Notification notification) {
        log.info("Sending notification to admins: {}", notification.getMessage());
        messagingTemplate.convertAndSend(
          "/topic/admin",
          toResponse(notification)
        );

        repository.save(notification);
    }

    public List<NotificationResponseDto> getAllNotifications() {
        return getNotifications().stream().map(NotificationMapper::toResponse).toList();
    }

    private List<Notification> getNotifications() {
        User loggedIn = authService.getCurrentUser();
        List<Notification> notifications;

        if (loggedIn.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName())))
            notifications = repository.findAllByRecipientIsNull();
        else
            notifications = repository.findByRecipient_Id(loggedIn.getId());

        return notifications;
    }

    public void markAsSeen() {
        List<Notification> notifications = getNotifications();
        notifications.stream().filter(notification -> !notification.getSeen()).forEach(notification -> {
            notification.setSeen(true);
            repository.save(notification);
        });
    }
}