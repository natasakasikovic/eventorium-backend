package com.iss.eventorium.solution.repositories;

import com.iss.eventorium.solution.models.SolutionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<SolutionHistory, Long> {
}
