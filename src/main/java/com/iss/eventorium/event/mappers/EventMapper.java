package com.iss.eventorium.event.mappers;

import com.iss.eventorium.event.dtos.event.*;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.user.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class EventMapper {

    private final ModelMapper modelMapper;
    private final EventTypeMapper eventTypeMapper;
    private final BudgetMapper budgetMapper;
    private final UserMapper userMapper;

    public EventSummaryResponseDto toSummaryResponse(Event event) {
        Long eventTypeId = event.getType() != null ? event.getType().getId() : null;
        return new EventSummaryResponseDto(event.getId(), eventTypeId, event.getName(), event.getCity().getName());
    }

    public PagedResponse<EventSummaryResponseDto> toPagedResponse(Page<Event> page) {
        return new PagedResponse<>(
                page.getContent().stream().map(this::toSummaryResponse).toList(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

    public Event fromRequest(EventRequestDto eventRequestDto) {
        return modelMapper.map(eventRequestDto, Event.class);
    }

    public EventResponseDto toResponse(Event event) {
        EventResponseDto dto = modelMapper.map(event, EventResponseDto.class);
        if(event.getBudget() != null)
            dto.setBudget(budgetMapper.toResponse(event.getBudget()));
        if(event.getType() != null)
            dto.setType(eventTypeMapper.toResponse(event.getType()));
        return dto;
    }

    public EditableEventDto toEditableEvent(Event event) {
        return modelMapper.map(event, EditableEventDto.class);
    }

    public EventDetailsDto toEventDetailsDto(Event event) {
        EventDetailsDto dto = modelMapper.map(event, EventDetailsDto.class);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yy");
        dto.setDate(event.getDate().format(formatter));
        dto.setPrivacy(event.getPrivacy().toString().toLowerCase());
        if (event.getType() == null)
            dto.setEventType("All");
        dto.setOrganizer(userMapper.toUserDetails(event.getOrganizer()));
        return dto;
    }

    public CalendarEventDto toCalendarEvent(Event event) {
        return modelMapper.map(event, CalendarEventDto.class);
    }
}
