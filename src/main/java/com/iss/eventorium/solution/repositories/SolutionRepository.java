package com.iss.eventorium.solution.repositories;

import com.iss.eventorium.solution.models.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SolutionRepository extends JpaRepository<Solution, Long>, JpaSpecificationExecutor<Solution> {
    Optional<Solution> findByCategoryId(Long id);
    boolean existsByCategory_Id(Long id);
}
