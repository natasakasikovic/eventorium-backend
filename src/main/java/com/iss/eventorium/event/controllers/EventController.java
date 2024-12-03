package com.iss.eventorium.event.controllers;

import com.iss.eventorium.event.dtos.EventSummaryResponseDto;
import com.iss.eventorium.event.services.EventService;
import com.iss.eventorium.shared.utils.EventFilter;
import com.iss.eventorium.shared.utils.PagedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;


@RestController
@RequestMapping("api/v1/events")
public class EventController {

    @Autowired
    private EventService service;

    @GetMapping("/top-five-events")
    public ResponseEntity<List<EventSummaryResponseDto>> getTopEvents(@RequestParam String city) {
        List<EventSummaryResponseDto> topEvents = service.getTopEvents(city);
        return new ResponseEntity<>(topEvents, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<Collection<EventSummaryResponseDto>> getEvents() {
        // TODO: call service -> eventService.getAll();
        Collection<EventSummaryResponseDto> events = List.of( new EventSummaryResponseDto(1L, "Event 1", "Novi Sad"),
                                                              new EventSummaryResponseDto(2L, "Event 2", "Novi Sad"));
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<EventSummaryResponseDto>> getEventsPaged(Pageable pageable) {
        // TODO: call service -> eventService.get(pageable)
        return ResponseEntity.ok().body(new PagedResponse<>(List.of(new EventSummaryResponseDto(1L, "Event 1", "Novi Sad")), 1, 3));
    }

    @GetMapping("/filter")
    public ResponseEntity<Collection<EventSummaryResponseDto>> filterEvents(EventFilter filter, Pageable pageable) {

        Collection<EventSummaryResponseDto> filteredEvents =  // TODO: delete 2 lines below and change with -> service.filterEvents(..params)
                List.of( new EventSummaryResponseDto(1L, "Music Festival", "Belgrade"),
                new EventSummaryResponseDto(2L, "Art Exhibition", "Novi Sad"));

        return new ResponseEntity<>(filteredEvents, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<EventSummaryResponseDto>> searchEvents(@RequestParam String keyword, Pageable pageable){

        Collection<EventSummaryResponseDto> events = List.of( // TODO: delete 2 lines below and change with -> service.searchEvents(keyword, pagable);
                        new EventSummaryResponseDto(1L, "Music Festival", "Novi Sad"),
                        new EventSummaryResponseDto(2L, "Art Exhibition", "Novi Sad"));

        return ResponseEntity.ok(events);
    }
}
