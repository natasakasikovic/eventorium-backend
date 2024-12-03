package com.iss.eventorium.event.services;

import com.iss.eventorium.event.dtos.EventSummaryResponseDto;
import com.iss.eventorium.event.mappers.EventMapper;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.repositories.EventRepository;
import com.iss.eventorium.event.repositories.EventSpecification;
import com.iss.eventorium.shared.utils.EventFilter;
import com.iss.eventorium.shared.utils.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EventService {

    private final EventRepository repository;

    public List<EventSummaryResponseDto> getTopEvents(String city) {
        Pageable pageable = PageRequest.of(0, 5);
        List<Event> events = repository.findTopFiveUpcomingEvents(city, pageable);
        return events.stream().map(EventMapper::toSummaryResponse).collect(Collectors.toList());
    }

    public List<EventSummaryResponseDto> getAll(){
        List<Event> events = repository.findAll();
        return events.stream().map(EventMapper::toSummaryResponse).collect(Collectors.toList());
    }

    public PagedResponse<EventSummaryResponseDto> searchEvents (String keyword, Pageable pageable) {
        if (keyword.isBlank()) {
            return EventMapper.toPagedResponse(repository.findAll(pageable));
        }
        return EventMapper.toPagedResponse(repository.findByNameContaining(keyword, pageable));
    }

    public PagedResponse<EventSummaryResponseDto> getEventsPaged (Pageable pageable) {
        return EventMapper.toPagedResponse(repository.findAll(pageable));
    }

    public PagedResponse<EventSummaryResponseDto> filterEvents (EventFilter filter, Pageable pageable) {
        Specification<Event> specification = EventSpecification.filterBy(filter);
        return EventMapper.toPagedResponse(repository.findAll(specification, pageable));
    }

}
