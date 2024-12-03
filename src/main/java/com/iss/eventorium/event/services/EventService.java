package com.iss.eventorium.event.services;

import com.iss.eventorium.event.dtos.EventSummaryResponseDto;
import com.iss.eventorium.event.mappers.EventMapper;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository repository;

    public List<EventSummaryResponseDto> getTopEvents(String city) {
        Pageable pageable = PageRequest.of(0, 5);
        List<Event> events = repository.findTopFiveUpcomingEvents(city, pageable);
        return events.stream().map(EventMapper::toSummaryResponse).collect(Collectors.toList());
    }

}
