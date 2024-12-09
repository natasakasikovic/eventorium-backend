package com.iss.eventorium.solution.models;

import com.iss.eventorium.shared.models.ImagePath;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table (name = "services")
@SQLDelete(sql = "UPDATE services SET is_deleted = true WHERE id = ?")
public class Service extends Solution {

    @Enumerated(EnumType.STRING)
    private ReservationType type;

    @Column(name="reservation_deadline", nullable = false)
    private LocalDate reservationDeadline;

    @Column(name="cancellation_deadline", nullable = false)
    private LocalDate cancellationDeadline;

    @Column(name="min_duration", nullable = false)
    private Integer minDuration;

    @Column(name="max_duration", nullable = false)
    private Integer maxDuration;
}
