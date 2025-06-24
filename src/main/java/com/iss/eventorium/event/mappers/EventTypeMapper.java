package com.iss.eventorium.event.mappers;

import com.iss.eventorium.category.mappers.CategoryMapper;
import com.iss.eventorium.event.dtos.eventtype.EventTypeRequestDto;
import com.iss.eventorium.event.dtos.eventtype.EventTypeResponseDto;
import com.iss.eventorium.event.models.EventType;
import com.iss.eventorium.shared.models.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventTypeMapper {

    private final ModelMapper modelMapper;
    private final CategoryMapper categoryMapper;

    public EventType fromRequest(EventTypeRequestDto eventTypeRequestDto) {
        return modelMapper.map(eventTypeRequestDto, EventType.class);
    }

    public EventTypeResponseDto toResponse(EventType eventType) {
        return EventTypeResponseDto.builder()
                .id(eventType.getId())
                .name(eventType.getName())
                .description(eventType.getDescription())
                .suggestedCategories(eventType.getSuggestedCategories().stream().map(categoryMapper::toResponse).toList())
                .build();
    }

    public PagedResponse<EventTypeResponseDto> toPagedResponse(Page<EventType> page) {
        return new PagedResponse<>(
                page.getContent().stream().map(this::toResponse).toList(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

    public EventType fromResponse(EventTypeResponseDto eventTypeResponseDto) {
        return modelMapper.map(eventTypeResponseDto, EventType.class);
    }
}
