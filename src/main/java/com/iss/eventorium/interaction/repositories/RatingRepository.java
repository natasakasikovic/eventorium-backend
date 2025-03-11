package com.iss.eventorium.interaction.repositories;

import com.iss.eventorium.interaction.models.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
}
