package com.iss.eventorium.solution.repositories;

import com.iss.eventorium.solution.models.Memento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface MementoRepository extends JpaRepository<Memento, Long>, JpaSpecificationExecutor<Memento> {
}
