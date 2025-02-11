package com.iss.eventorium.event.services;

import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.category.services.CategoryService;
import com.iss.eventorium.event.dtos.eventtype.EventTypeRequestDto;
import com.iss.eventorium.event.mappers.EventTypeMapper;
import com.iss.eventorium.event.dtos.eventtype.EventTypeResponseDto;
import com.iss.eventorium.event.models.EventType;
import com.iss.eventorium.event.repositories.EventTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventTypeService {
    private final EventTypeRepository repository;
    private final CategoryService categoryService;

    public EventType find(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event type not found"));
    }

    public List<EventTypeResponseDto> getEventTypes() {
        return repository.findByDeletedFalse().stream().map(EventTypeMapper::toResponse).toList();
    }

    public EventTypeResponseDto getEventType(Long id) {
        return EventTypeMapper.toResponse(find(id));
    }

    public EventTypeResponseDto createEventType(EventTypeRequestDto request) {
        return EventTypeMapper.toResponse(repository.save(EventTypeMapper.fromRequest(request)));
    }

    public EventTypeResponseDto updateEventType(Long id, EventTypeRequestDto request) {
        EventType eventType = find(id);

        eventType.setDescription(request.getDescription());
        List<Category> categories = request.getSuggestedCategories()
                                    .stream()
                                    .map(category -> categoryService.find(category.getId()))
                                    .toList();
        eventType.setSuggestedCategories(categories);

        return EventTypeMapper.toResponse(repository.save(eventType));
    }

    public void deleteEventType(Long id) {
        EventType eventType = find(id);
        eventType.setDeleted(true);
        repository.save(eventType);
    }
}
