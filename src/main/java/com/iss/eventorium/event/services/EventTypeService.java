package com.iss.eventorium.event.services;

import com.iss.eventorium.event.dtos.EventTypeRequestDto;
import com.iss.eventorium.event.mappers.EventTypeMapper;
import com.iss.eventorium.event.dtos.EventTypeResponseDto;
import com.iss.eventorium.event.models.EventType;
import com.iss.eventorium.event.repositories.EventTypeRepository;
import com.iss.eventorium.shared.utils.PagedResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventTypeService {
    private final EventTypeRepository eventTypeRepository;

    public List<EventTypeResponseDto> getEventTypes() {
        return eventTypeRepository.findByDeletedFalse().stream()
                .map(EventTypeMapper::toResponse)
                .toList();
    }

    public PagedResponse<EventTypeResponseDto> getEventTypesPaged(Pageable pageable) {
        return EventTypeMapper.toPagedResponse(eventTypeRepository.findByDeletedFalse(pageable));
    }

    public EventTypeResponseDto getEventType(Long id) {
        EventType eventType = eventTypeRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Event type with id " + id + " not found"));
        return EventTypeMapper.toResponse(eventType);
    }

    public EventTypeResponseDto createEventType(EventTypeRequestDto eventTypeRequestDto) {
        EventType created = EventTypeMapper.fromRequest(eventTypeRequestDto);
        return EventTypeMapper.toResponse(eventTypeRepository.save(created));
    }

    public EventTypeResponseDto updateEventType(Long id, EventTypeRequestDto eventTypeRequestDto) {
        EventType eventType = eventTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event type with id " + id + " not found"));

        eventType.setName(eventTypeRequestDto.getName());
        eventType.setDescription(eventTypeRequestDto.getDescription());

        return EventTypeMapper.toResponse(eventTypeRepository.save(eventType));
    }

    public void deleteEventType(Long id) {
        EventType eventType = eventTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event type with id " + id + " not found"));

        eventType.setDeleted(true);
        eventTypeRepository.save(eventType);
    }
}
