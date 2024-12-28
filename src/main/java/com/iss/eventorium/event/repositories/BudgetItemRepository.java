package com.iss.eventorium.event.repositories;

import com.iss.eventorium.event.models.BudgetItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetItemRepository extends JpaRepository<BudgetItem, Long> {
}
