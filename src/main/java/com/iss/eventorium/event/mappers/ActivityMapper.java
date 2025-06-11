package com.iss.eventorium.event.mappers;

import com.iss.eventorium.event.dtos.agenda.ActivityRequestDto;
import com.iss.eventorium.event.dtos.agenda.ActivityResponseDto;
import com.iss.eventorium.event.models.Activity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

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
}
