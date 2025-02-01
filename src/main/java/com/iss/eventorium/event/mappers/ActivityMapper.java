package com.iss.eventorium.event.mappers;

import com.iss.eventorium.event.dtos.agenda.ActivityRequestDto;
import com.iss.eventorium.event.dtos.agenda.ActivityResponseDto;
import com.iss.eventorium.event.models.Activity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ActivityMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public ActivityMapper(ModelMapper modelMapper) {
        ActivityMapper.modelMapper = modelMapper;
    }

    public static Activity fromRequest(ActivityRequestDto dto) {
        return modelMapper.map(dto, Activity.class);
    }

    public static ActivityResponseDto toResponse(Activity activity) {
        return modelMapper.map(activity, ActivityResponseDto.class);
    }
}
