package com.iss.eventorium.event.controllers;

import com.iss.eventorium.event.dtos.EventSummaryResponseDto;
import com.iss.eventorium.event.services.EventService;
import com.iss.eventorium.shared.utils.EventFilter;
import com.iss.eventorium.shared.utils.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/events")
public class EventController {

    private final EventService service;

    @GetMapping("/top-five-events")
    public ResponseEntity<List<EventSummaryResponseDto>> getTopEvents(@RequestParam String city) {
        return new ResponseEntity<>(service.getTopEvents(city), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<Collection<EventSummaryResponseDto>> getEvents() {
        return new ResponseEntity<>(service.getAll(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<EventSummaryResponseDto>> getEventsPaged(Pageable pageable) {
        return ResponseEntity.ok(service.getEventsPaged(pageable));
    }

    @GetMapping("/filter")
    public ResponseEntity<PagedResponse<EventSummaryResponseDto>> filterEvents(EventFilter filter, Pageable pageable) {
        return ResponseEntity.ok(service.filterEvents(filter, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<EventSummaryResponseDto>> searchEvents(@RequestParam (required = false) String keyword, Pageable pageable){
        return ResponseEntity.ok(service.searchEvents(keyword, pageable));
    }

}
