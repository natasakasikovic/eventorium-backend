package com.iss.eventorium.user.mappers;

import com.iss.eventorium.user.dtos.AuthRequestDto;
import com.iss.eventorium.user.models.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public UserMapper(ModelMapper modelMapper) { UserMapper.modelMapper = modelMapper; }

    public static User fromRequest(AuthRequestDto requestDto) {
        return modelMapper.map(requestDto, User.class);
    }
}
