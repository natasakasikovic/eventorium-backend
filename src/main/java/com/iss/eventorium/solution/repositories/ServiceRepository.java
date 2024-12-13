package com.iss.eventorium.solution.repositories;

import com.iss.eventorium.solution.models.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    @Query("SELECT s FROM Service s JOIN s.reviews r WHERE s.status = 'ACCEPTED' GROUP BY s.id ORDER BY AVG(r.rating) DESC")
    List<Service> findTopFiveServices(Pageable pageable);
    Optional<Service> findByCategoryId(Long categoryId);

}
