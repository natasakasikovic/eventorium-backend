package com.iss.eventorium.interaction.models;

import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.user.models.User;
import jakarta.persistence.*;
import lombok.*;

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
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private CommentType commentType;
}
