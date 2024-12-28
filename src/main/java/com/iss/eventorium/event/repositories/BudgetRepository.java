package com.iss.eventorium.event.repositories;

import com.iss.eventorium.event.models.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
}
