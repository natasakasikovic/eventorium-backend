package com.iss.eventorium.event.mappers;

import com.iss.eventorium.event.dtos.event.*;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.user.mappers.UserMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

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

    public static EventDetailsDto toEventDetailsDto(Event event) {
        EventDetailsDto dto = modelMapper.map(event, EventDetailsDto.class);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yy");
        dto.setDate(event.getDate().format(formatter));
        dto.setPrivacy(event.getPrivacy().toString().toLowerCase());
        if (event.getType() == null)
            dto.setEventType("All");
        dto.setOrganizer(UserMapper.toUserDetails(event.getOrganizer()));
        return dto;
    }

    public static CalendarEventDto toCalendarEvent(Event event) {
        return CalendarEventDto.builder()
                .id(event.getId())
                .name(event.getName())
                .date(event.getDate())
                .build();
    }
}
