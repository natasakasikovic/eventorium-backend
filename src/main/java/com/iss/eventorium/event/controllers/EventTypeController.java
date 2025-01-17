package com.iss.eventorium.event.controllers;

import com.iss.eventorium.event.dtos.eventtype.EventTypeRequestDto;
import com.iss.eventorium.event.dtos.eventtype.EventTypeResponseDto;
import com.iss.eventorium.event.services.EventTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/event-types")
@RequiredArgsConstructor
@CrossOrigin
public class EventTypeController {

    private final EventTypeService eventTypeService;

    @GetMapping("/all")
    public ResponseEntity<List<EventTypeResponseDto>> getEventTypes() {
        return ResponseEntity.ok(eventTypeService.getEventTypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventTypeResponseDto> getEventType(@PathVariable Long id) {
        return ResponseEntity.ok(eventTypeService.getEventType(id));
    }

    @PostMapping
    public ResponseEntity<EventTypeResponseDto> createEventType(@Valid @RequestBody EventTypeRequestDto requestDto) {
        return new ResponseEntity<>(eventTypeService.createEventType(requestDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventTypeResponseDto> updateEventType(
            @Valid @RequestBody EventTypeRequestDto requestDto,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(eventTypeService.updateEventType(id, requestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventType(@PathVariable Long id) {
        eventTypeService.deleteEventType(id);
        return ResponseEntity.noContent().build();
    }
}
