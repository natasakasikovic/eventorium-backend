package com.iss.eventorium.event.mappers;

import com.iss.eventorium.event.dtos.event.EventRequestDto;
import com.iss.eventorium.event.dtos.event.EventResponseDto;
import com.iss.eventorium.event.dtos.event.EventSummaryResponseDto;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.shared.utils.PagedResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    private static ModelMapper modelMapper;

    @Autowired
    public EventMapper(ModelMapper modelMapper) {
        EventMapper.modelMapper = modelMapper;
    }

    public static EventSummaryResponseDto toSummaryResponse(Event event) {
        return new EventSummaryResponseDto(event.getId(), event.getName(), event.getCity().getName());
    }

    public static PagedResponse<EventSummaryResponseDto> toPagedResponse(Page<Event> page) {
        return new PagedResponse<>(
                page.stream().map(EventMapper::toSummaryResponse).toList(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

    public static Event fromRequest(EventRequestDto eventRequestDto) {
        return modelMapper.map(eventRequestDto, Event.class);
    }

    public static EventResponseDto toResponse(Event event) {
        EventResponseDto dto = modelMapper.map(event, EventResponseDto.class);
        if(event.getBudget() != null)
            dto.setBudget(BudgetMapper.toResponse(event.getBudget()));
        if(event.getType() != null)
            dto.setType(EventTypeMapper.toResponse(event.getType()));
        return dto;
    }

}
