package com.iss.eventorium.interaction.repositories;

import com.iss.eventorium.interaction.models.Rating;
import com.iss.eventorium.shared.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
}
