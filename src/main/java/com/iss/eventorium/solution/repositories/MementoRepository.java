package com.iss.eventorium.solution.repositories;

import com.iss.eventorium.solution.models.Memento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MementoRepository extends JpaRepository<Memento, Long> {

    List<Memento> findBySolutionIdOrderByValidFromDesc(Long id);
    List<Memento> findBySolutionId(Long id);
}
