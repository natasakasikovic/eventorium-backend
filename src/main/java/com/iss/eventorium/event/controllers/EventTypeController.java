package com.iss.eventorium.event.controllers;

import com.iss.eventorium.event.dtos.eventtype.EventTypeRequestDto;
import com.iss.eventorium.event.dtos.eventtype.EventTypeResponseDto;
import com.iss.eventorium.event.services.EventTypeService;
import com.iss.eventorium.shared.utils.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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

    @GetMapping()
    public ResponseEntity<PagedResponse<EventTypeResponseDto>> getEventTypesPaged(Pageable pageable) {
        return ResponseEntity.ok(eventTypeService.getEventTypesPaged(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventTypeResponseDto> getEventType(@PathVariable Long id) {
        return ResponseEntity.ok(eventTypeService.getEventType(id));
    }

    @PostMapping
    public ResponseEntity<EventTypeResponseDto> createEventType(@Valid @RequestBody EventTypeRequestDto requestDto) {
        return ResponseEntity.ok(eventTypeService.createEventType(requestDto));
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
