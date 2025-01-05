package com.iss.eventorium.user.mappers;

import com.iss.eventorium.user.dtos.*;
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

    public static User fromRequest(AuthRequestDto requestDto) {
        return modelMapper.map(requestDto, User.class);
    }

    public static AuthResponseDto toResponse(User user) {
        return modelMapper.map(user, AuthResponseDto.class);
    }

    public static AccountDetailsDto toAccountDetailsDto(User user) {
        return AccountDetailsDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getPerson().getName())
                .lastname(user.getPerson().getLastname())
                .city(user.getPerson().getCity())
                .address(user.getPerson().getAddress())
                .phoneNumber(user.getPerson().getPhoneNumber())
                .role(user.getRoles().get(0).getName().replace("_", " "))
                .build();
    }
}
