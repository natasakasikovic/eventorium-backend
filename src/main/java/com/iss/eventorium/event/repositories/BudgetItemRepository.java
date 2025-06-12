package com.iss.eventorium.event.repositories;

import com.iss.eventorium.event.models.BudgetItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BudgetItemRepository extends JpaRepository<BudgetItem, Long>, JpaSpecificationExecutor<BudgetItem> {
}