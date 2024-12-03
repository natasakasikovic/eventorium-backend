package com.iss.eventorium.event.mappers;

import com.iss.eventorium.event.dtos.EventTypeRequestDto;
import com.iss.eventorium.event.dtos.EventTypeResponseDto;
import com.iss.eventorium.event.models.EventType;
import com.iss.eventorium.shared.utils.PagedResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class EventTypeMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public EventTypeMapper(ModelMapper modelMapper) {
        EventTypeMapper.modelMapper = modelMapper;
    }

    public static EventType fromRequest(EventTypeRequestDto eventTypeRequestDto) {
        return modelMapper.map(eventTypeRequestDto, EventType.class);
    }

    public static EventTypeResponseDto toResponse(EventType eventType) {
        return modelMapper.map(eventType, EventTypeResponseDto.class);
    }

    public static PagedResponse<EventTypeResponseDto> toPagedResponse(Page<EventType> page) {
        return new PagedResponse<>(
                page.stream().map(EventTypeMapper::toResponse).toList(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}
