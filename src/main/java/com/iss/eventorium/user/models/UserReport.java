package com.iss.eventorium.user.models;

import com.iss.eventorium.shared.models.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reports")
public class UserReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "offender_id", referencedColumnName = "id")
    private User offender;

    @ManyToOne
    @JoinColumn(name = "reporter_id", referencedColumnName = "id")
    private User reporter;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;
}