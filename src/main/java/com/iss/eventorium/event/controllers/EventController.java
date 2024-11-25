package com.iss.eventorium.event.controllers;

import com.iss.eventorium.event.dtos.EventSummaryResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("api/v1/events")
public class EventController {

    @GetMapping("/top-five-events")
    public ResponseEntity<Collection<EventSummaryResponseDto>> getTopEvents() {

        // Collection<EventSummaryResponseDto> topEvents = eventService.getTopEvents();
        // TODO: uncomment line above once service is implemented and also delete dummy services below
        Collection<EventSummaryResponseDto> topEvents = List.of(
                new EventSummaryResponseDto(1L, "Event 1", "Novi Sad"),
                new EventSummaryResponseDto(2L, "Event 2", "Novi Sad"),
                new EventSummaryResponseDto(3L, "Event 3", "Novi Sad"),
                new EventSummaryResponseDto(4L, "Event 4", "Novi Sad"),
                new EventSummaryResponseDto(5L, "Event 5", "Novi Sad")
        );

        return new ResponseEntity<>(topEvents, HttpStatus.OK);
    }

}
