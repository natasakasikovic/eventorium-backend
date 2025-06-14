package com.iss.eventorium.event.controllers;

import com.iss.eventorium.event.api.EventTypeApi;
import com.iss.eventorium.event.dtos.eventtype.EventTypeRequestDto;
import com.iss.eventorium.event.dtos.eventtype.EventTypeResponseDto;
import com.iss.eventorium.event.services.EventTypeService;
import com.iss.eventorium.shared.models.ImagePath;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/event-types")
@RequiredArgsConstructor
public class EventTypeController implements EventTypeApi {

    private final EventTypeService service;

    @GetMapping("/all")
    public ResponseEntity<List<EventTypeResponseDto>> getEventTypes() {
        return ResponseEntity.ok(service.getEventTypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventTypeResponseDto> getEventType(@PathVariable Long id) {
        return ResponseEntity.ok(service.getEventType(id));
    }

    @PostMapping
    public ResponseEntity<EventTypeResponseDto> createEventType(@Valid @RequestBody EventTypeRequestDto requestDto) {
        return new ResponseEntity<>(service.createEventType(requestDto), HttpStatus.CREATED);
    }

    @PostMapping(path = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadImage(@PathVariable Long id, @RequestParam("image") MultipartFile file) {
        service.uploadImage(id, file);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        ImagePath path = service.getImagePath(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(path.getContentType()))
                .body(service.getImage(path));
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<byte[]> updateImage(@PathVariable Long id, @RequestParam("image") MultipartFile file) {
        service.uploadImage(id, file);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventTypeResponseDto> updateEventType(@PathVariable Long id, @Valid @RequestBody EventTypeRequestDto requestDto) {
        return ResponseEntity.ok(service.updateEventType(id, requestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventType(@PathVariable Long id) {
        service.deleteEventType(id);
        return ResponseEntity.noContent().build();
    }
}
