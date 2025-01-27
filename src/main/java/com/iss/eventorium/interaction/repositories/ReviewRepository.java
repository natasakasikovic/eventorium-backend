package com.iss.eventorium.interaction.repositories;

import com.iss.eventorium.interaction.models.Review;
import com.iss.eventorium.shared.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByStatus(Status status);
}
