package com.iss.eventorium.user.mappers;

import com.iss.eventorium.user.dtos.UserReportRequestDto;
import com.iss.eventorium.user.models.UserReport;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserReportMapper {

    private static ModelMapper modelMapper;

    @Autowired
    public UserReportMapper(ModelMapper modelMapper) {
        UserReportMapper.modelMapper = modelMapper;
    }

    public static UserReport fromRequest(UserReportRequestDto request) {
        return modelMapper.map(request, UserReport.class);
    }
}