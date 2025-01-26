package com.iss.eventorium.user.mappers;

import com.iss.eventorium.user.dtos.user.ChatUserDetailsDto;
import com.iss.eventorium.user.dtos.auth.QuickRegistrationRequestDto;
import com.iss.eventorium.user.dtos.auth.AuthRequestDto;
import com.iss.eventorium.user.dtos.auth.AuthResponseDto;
import com.iss.eventorium.shared.mappers.CityMapper;
import com.iss.eventorium.user.dtos.user.AccountDetailsDto;
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

    public static ChatUserDetailsDto toChatUserDetails(User user) {
        return ChatUserDetailsDto.builder()
                .id(user.getId())
                .name(user.getPerson().getName())
                .lastname(user.getPerson().getLastname())
                .build();
    }

    public static AccountDetailsDto toAccountDetails(User user) {
        return AccountDetailsDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getPerson().getName())
                .lastname(user.getPerson().getLastname())
                .city(CityMapper.toResponse(user.getPerson().getCity()))
                .address(user.getPerson().getAddress())
                .phoneNumber(user.getPerson().getPhoneNumber())
                .role(user.getRoles().get(0).getName().replace("_", " "))
                .build();
    }
}
