package com.iss.eventorium.event.repositories;

import com.iss.eventorium.event.models.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventTypeRepository extends JpaRepository<EventType, Long> {
    List<EventType> findByDeletedFalse();
    boolean existsByName(String name);
}
