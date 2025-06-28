package com.iss.eventorium.user.mappers;

import com.iss.eventorium.shared.mappers.CityMapper;
import com.iss.eventorium.user.dtos.auth.AuthRequestDto;
import com.iss.eventorium.user.dtos.auth.AuthResponseDto;
import com.iss.eventorium.user.dtos.auth.QuickRegistrationRequestDto;
import com.iss.eventorium.user.dtos.user.AccountDetailsDto;
import com.iss.eventorium.user.dtos.user.UserDetailsDto;
import com.iss.eventorium.user.models.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final ModelMapper modelMapper;
    private final CityMapper cityMapper;

    public User fromRequest(QuickRegistrationRequestDto request) {
        return modelMapper.map(request, User.class);
    }

    public User fromRequest(AuthRequestDto requestDto) {
        return modelMapper.map(requestDto, User.class);
    }

    public AuthResponseDto toResponse(User user) {
        return modelMapper.map(user, AuthResponseDto.class);
    }

    public UserDetailsDto toUserDetails(User user) {
        return UserDetailsDto.builder()
                .id(user.getId())
                .name(user.getPerson().getName())
                .lastname(user.getPerson().getLastname())
                .build();
    }

    public AccountDetailsDto toAccountDetails(User user) {
        return AccountDetailsDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getPerson().getName())
                .lastname(user.getPerson().getLastname())
                .city(cityMapper.toResponse(user.getPerson().getCity()))
                .address(user.getPerson().getAddress())
                .phoneNumber(user.getPerson().getPhoneNumber())
                .role(user.getRoles().get(0).getName().replace("_", " "))
                .build();
    }
}
