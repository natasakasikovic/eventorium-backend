package com.iss.eventorium.interaction.repositories;

import com.iss.eventorium.interaction.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
