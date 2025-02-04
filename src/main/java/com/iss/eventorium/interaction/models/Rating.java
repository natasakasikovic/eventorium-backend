package com.iss.eventorium.interaction.models;

import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.user.models.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ratings")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime creationDate;

    @Column(nullable = false)
    @Min(value = 1)
    @Max(value = 5)
    private Integer rating;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;
}
