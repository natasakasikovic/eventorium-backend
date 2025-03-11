package com.iss.eventorium.solution.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;

import java.util.Objects;


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

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type, specialties, reservationDeadline, cancellationDeadline, minDuration, maxDuration);
    }
}
