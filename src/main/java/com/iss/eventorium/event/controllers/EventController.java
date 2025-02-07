package com.iss.eventorium.event.controllers;

import com.iss.eventorium.event.dtos.agenda.ActivityRequestDto;
import com.iss.eventorium.event.dtos.agenda.ActivityResponseDto;
import com.iss.eventorium.event.dtos.event.*;
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
@CrossOrigin
@RequestMapping("api/v1/events")
public class EventController {

    private final EventService service;

    @GetMapping("/{id}")
    public ResponseEntity<EventDetailsDto> getEvent(@PathVariable("id") Long id) {
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

    @GetMapping
    public ResponseEntity<PagedResponse<EventSummaryResponseDto>> getEventsPaged(Pageable pageable) {
        return ResponseEntity.ok(service.getEventsPaged(pageable));
    }

    @GetMapping("/filter")
    public ResponseEntity<PagedResponse<EventSummaryResponseDto>> filterEvents(@Valid @ModelAttribute EventFilterDto filter, Pageable pageable) {
        return ResponseEntity.ok(service.filterEvents(filter, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<EventSummaryResponseDto>> searchEvents(@RequestParam (required = false) String keyword, Pageable pageable){
        return ResponseEntity.ok(service.searchEvents(keyword, pageable));
    }

    @GetMapping("/drafted")
    public ResponseEntity<List<EventResponseDto>> draftedEvents() {
        return ResponseEntity.ok(service.getDraftedEvents());
    }
  
    @GetMapping("/search/all")
    public ResponseEntity<List<EventSummaryResponseDto>> searchEvents(@RequestParam (required = false) String keyword) {
        return  ResponseEntity.ok(service.searchEvents(keyword));
    }

    @PostMapping
    public ResponseEntity<EventResponseDto> createEvent(@Valid @RequestBody EventRequestDto eventRequestDto) {
        return new ResponseEntity<>(service.createEvent(eventRequestDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/agenda")
    public ResponseEntity<Void> createAgenda(@Valid @RequestBody List<ActivityRequestDto> requestDto,
                                          @PathVariable Long id) {
        service.createAgenda(id, requestDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}/agenda")
    public ResponseEntity<List<ActivityResponseDto>> getAgenda(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAgenda(id));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getEventDetailsPdf(@PathVariable Long id) {
        HttpHeaders headers = ResponseHeaderUtils.createPdfHeaders("event_details.pdf");
        return new ResponseEntity<>(service.generateEventDetailsPdf(id), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}/guest-list-pdf")
    public ResponseEntity<byte[]> getGuestListPdf(@PathVariable Long id) {
        HttpHeaders headers = ResponseHeaderUtils.createPdfHeaders("guest-list.pdf");
        return new ResponseEntity<>(service.generateGuestListPdf(id), headers, HttpStatus.OK);
    }

}
