package com.iss.eventorium.notifications.models;

import com.iss.eventorium.user.models.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notifications")
@ToString
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean seen = false;

    @ManyToOne
    @JoinColumn(name = "recipient_id", referencedColumnName = "id")
    private User recipient = null;

    @Transient
    private NotificationType type;

    public Notification(String title, String message, NotificationType type) {
        this.title = title;
        this.message = message;
        this.type = type;
    }

    public Notification(Notification notification) {
        id = notification.getId();
        title = notification.getTitle();
        type = notification.getType();
        recipient = notification.getRecipient();
        message = notification.getMessage();
        timestamp = notification.getTimestamp();
        seen = notification.getSeen();
    }
}
