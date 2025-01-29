package com.iss.eventorium.notifications.repositories;

import com.iss.eventorium.notifications.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByRecipientIsNullOrderByTimestampDesc();
    List<Notification> findByRecipient_IdOrderByTimestampDesc(Long id);
}
