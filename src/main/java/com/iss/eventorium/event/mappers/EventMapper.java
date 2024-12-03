package com.iss.eventorium.event.mappers;

import com.iss.eventorium.event.dtos.EventSummaryResponseDto;
import com.iss.eventorium.event.models.Event;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    private static ModelMapper modelMapper;

    @Autowired
    public EventMapper(ModelMapper modelMapper) {
        EventMapper.modelMapper = modelMapper;
    }

    public static EventSummaryResponseDto toSummaryResponse(Event event) {
        return new EventSummaryResponseDto(event.getId(), event.getName(), event.getLocation().getCity());
    }

}
