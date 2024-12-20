package com.iss.eventorium.user.mappers;

import com.iss.eventorium.user.dtos.QuickRegistrationRequestDto;
import com.iss.eventorium.user.models.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public UserMapper(ModelMapper modelMapper) { UserMapper.modelMapper = modelMapper; }

    public static User fromRequest(QuickRegistrationRequestDto request) {
        return modelMapper.map(request, User.class);
    }

}