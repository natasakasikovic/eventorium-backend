package com.iss.eventorium.user.mappers;

import com.iss.eventorium.user.dtos.ProviderResponseDto;
import com.iss.eventorium.user.dtos.QuickRegistrationRequestDto;
import com.iss.eventorium.user.dtos.AuthRequestDto;
import com.iss.eventorium.user.dtos.AuthResponseDto;
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

    public static ProviderResponseDto toProviderResponse(User user) {
        return ProviderResponseDto.builder()
                .id(user.getId())
                .name(user.getPerson().getName())
                .lastname(user.getPerson().getLastname())
                .build();
    }
}
