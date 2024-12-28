package com.iss.eventorium.event.repositories;

import com.iss.eventorium.event.dtos.EventSummaryResponseDto;
import com.iss.eventorium.event.models.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    @Query("SELECT e FROM Event e WHERE e.city.name = :city AND e.privacy = 'OPEN' AND e.date > CURRENT_DATE ORDER BY e.date DESC")
    List<Event> findTopFiveUpcomingEvents(@Param("city") String city, Pageable pageable);

    Page<Event> findByNameContainingAllIgnoreCase(String keyword, Pageable pageable);

    List<Event> findByIsDraftTrueAndOrganizer_Id(Long organizerId);
}
