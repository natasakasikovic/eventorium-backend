package com.iss.eventorium.solution.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDate;

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

    @Column(nullable = false)
    private LocalDate reservationDeadline;

    @Column(nullable = false)
    private LocalDate cancellationDeadline;

    @Column(nullable = false)
    private Integer minDuration;

    @Column(nullable = false)
    private Integer maxDuration;

}
