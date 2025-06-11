package com.iss.eventorium.solution.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;


@EqualsAndHashCode(callSuper = true)
@Data
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
    @Size(max = 750)
    private String specialties;

    @Column(name="reservation_deadline", nullable = false)
    private Integer reservationDeadline;

    @Column(name="cancellation_deadline", nullable = false)
    private Integer cancellationDeadline;

    @Column(name="min_duration", nullable = false)
    private Integer minDuration;

    @Column(name="max_duration", nullable = false)
    private Integer maxDuration;

    @Override
    public void restore(Memento memento) {
        setName(memento.getName());
        setPrice(memento.getPrice());
        setDescription(memento.getDescription());
        setSpecialties(memento.getSpecialties());
        setDiscount(memento.getDiscount());
        setEventTypes(memento.getEventTypes());
        setIsVisible(memento.isVisible());
        setIsAvailable(memento.isAvailable());
        setReservationDeadline(memento.getReservationDeadline());
        setType(memento.getType());
        setCancellationDeadline(memento.getCancellationDeadline());
        setMinDuration(memento.getMinDuration());
        setMaxDuration(memento.getMaxDuration());
    }
}
