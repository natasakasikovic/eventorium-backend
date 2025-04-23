package com.iss.eventorium.interaction.models;

import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.user.models.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(max = 100)
    private String comment;

    @Column(nullable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private CommentType commentType;

    @Column(name = "commentable_id", nullable = false)
    private Long commentableId;
}
