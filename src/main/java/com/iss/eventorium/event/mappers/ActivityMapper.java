package com.iss.eventorium.event.mappers;

import com.iss.eventorium.event.dtos.agenda.ActivityRequestDto;
import com.iss.eventorium.event.dtos.agenda.ActivityResponseDto;
import com.iss.eventorium.event.dtos.agenda.AgendaResponseDto;
import com.iss.eventorium.event.models.Activity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ActivityMapper {

    private final ModelMapper modelMapper;

    public Activity fromRequest(ActivityRequestDto dto) {
        return modelMapper.map(dto, Activity.class);
    }

    public ActivityResponseDto toResponse(Activity activity) {
        return modelMapper.map(activity, ActivityResponseDto.class);
    }

    public List<ActivityResponseDto> toResponse(List<Activity> activities) {
        return activities.stream().map(this::toResponse).toList();
    }

    public AgendaResponseDto toAgendaResponse(Long eventId, List<Activity> activities) {
        return AgendaResponseDto.builder()
                .eventId(eventId)
                .activities(toResponse(activities))
                .build();
    }
}
