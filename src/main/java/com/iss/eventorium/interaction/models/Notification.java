package com.iss.eventorium.interaction.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String message;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private Boolean seen;

    @JsonIgnore
    private NotificationType type;

    public Notification(String message, NotificationType type) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.seen = false;
        this.type = type;
    }
}
