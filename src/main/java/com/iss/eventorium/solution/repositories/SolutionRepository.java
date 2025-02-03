package com.iss.eventorium.solution.repositories;

import com.iss.eventorium.solution.models.Solution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SolutionRepository extends JpaRepository<Solution, Long> {
    Optional<Solution> findByCategoryId(Long id);
}
