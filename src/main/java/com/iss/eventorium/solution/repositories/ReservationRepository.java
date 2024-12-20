package com.iss.eventorium.solution.repositories;

import com.iss.eventorium.solution.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reservation r WHERE r.service.id = :serviceId")
    boolean existsByServiceId(Long serviceId);
}
