package com.iss.eventorium.event.controllers;

import com.iss.eventorium.event.dtos.agenda.ActivityRequestDto;
import com.iss.eventorium.event.dtos.agenda.ActivityResponseDto;
import com.iss.eventorium.event.dtos.event.*;
import com.iss.eventorium.event.dtos.statistics.EventRatingsStatisticsDto;
import com.iss.eventorium.event.services.EventService;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.shared.utils.ResponseHeaderUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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

    @GetMapping("/{id}")
    public ResponseEntity<EditableEventDto> getEvent(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.getEvent(id));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<EventDetailsDto> getEventDetails(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.getEventDetails(id));
    }

    @GetMapping("/top-five-events")
    public ResponseEntity<List<EventSummaryResponseDto>> getTopEvents() {
        return new ResponseEntity<>(service.getTopEvents(), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<Collection<EventSummaryResponseDto>> getEvents() {
        return new ResponseEntity<>(service.getAll(), HttpStatus.OK);
    }

    @GetMapping("/passed")
    public ResponseEntity<List<EventTableOverviewDto>> getPassedEvents() {
        return new ResponseEntity<>(service.getPassedEvents(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<EventSummaryResponseDto>> getEventsPaged(Pageable pageable) {
        return ResponseEntity.ok(service.getEventsPaged(pageable));
    }

    @GetMapping("/filter")
    public ResponseEntity<PagedResponse<EventSummaryResponseDto>> filterEvents(@Valid @ModelAttribute EventFilterDto filter, Pageable pageable) {
        return ResponseEntity.ok(service.filterEventsPaged(filter, pageable));
    }

    @GetMapping("/filter/all")
    public ResponseEntity<List<EventSummaryResponseDto>> filterEvents(@Valid @ModelAttribute EventFilterDto filter) {
        return ResponseEntity.ok(service.filterEvents(filter));
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<EventSummaryResponseDto>> searchEvents(@RequestParam (required = false) String keyword, Pageable pageable){
        return ResponseEntity.ok(service.searchEventsPaged(keyword, pageable));
    }

    @GetMapping("/future")
    public ResponseEntity<List<EventResponseDto>> getFutureEvents() {
        return ResponseEntity.ok(service.getFutureEvents());
    }
  
    @GetMapping("/search/all")
    public ResponseEntity<List<EventSummaryResponseDto>> searchEvents(@RequestParam (required = false) String keyword) {
        return ResponseEntity.ok(service.searchEvents(keyword));
    }

    @PostMapping
    public ResponseEntity<EventResponseDto> createEvent(@Valid @RequestBody EventRequestDto eventRequestDto) {
        return new ResponseEntity<>(service.createEvent(eventRequestDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/agenda")
    public ResponseEntity<Void> createAgenda(@Valid @RequestBody List<ActivityRequestDto> request, @PathVariable Long id) {
        service.createAgenda(id, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}/agenda")
    public ResponseEntity<List<ActivityResponseDto>> getAgenda(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAgenda(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateEvent(@PathVariable Long id, @Valid @RequestBody UpdateEventRequestDto request) {
        this.service.updateEvent(id, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getEventDetailsPdf(@PathVariable Long id) {
        HttpHeaders headers = ResponseHeaderUtils.createPdfHeaders("event_details.pdf");
        return new ResponseEntity<>(service.generateEventDetailsPdf(id), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}/guest-list-pdf")
    public ResponseEntity<byte[]> getGuestListPdf(@PathVariable Long id) {
        HttpHeaders headers = ResponseHeaderUtils.createPdfHeaders("guest_list.pdf");
        return new ResponseEntity<>(service.generateGuestListPdf(id), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}/statistics")
    public ResponseEntity<EventRatingsStatisticsDto> getEventRatingStatistics(@PathVariable Long id) {
        return new ResponseEntity<>(service.getEventRatingStatistics(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/pdf-statistics")
    public ResponseEntity<byte[]> getEventStatisticsPdf(@PathVariable Long id) {
        HttpHeaders headers = ResponseHeaderUtils.createPdfHeaders("event_statistics.pdf");
        return new ResponseEntity<>(service.generateEventStatisticsPdf(id), headers, HttpStatus.OK);
    }
}
