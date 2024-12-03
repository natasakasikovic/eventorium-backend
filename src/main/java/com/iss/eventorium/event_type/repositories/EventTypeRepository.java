package com.iss.eventorium.event_type.repositories;

import com.iss.eventorium.event_type.models.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventTypeRepository extends JpaRepository<EventType, Long> {
    Optional<EventType> findByIdAndDeletedFalse(Long id);
    List<EventType> findByDeletedFalse();
    Page<EventType> findByDeletedFalse(Pageable pageable);
}
