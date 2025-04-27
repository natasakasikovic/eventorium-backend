package com.iss.eventorium.interaction.mappers;

import com.iss.eventorium.interaction.dtos.ratings.CreateRatingRequestDto;
import com.iss.eventorium.interaction.dtos.ratings.RatingResponseDto;
import com.iss.eventorium.interaction.models.Rating;
import com.iss.eventorium.user.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RatingMapper {

    private final ModelMapper modelMapper;
    private final UserMapper userMapper;

    public RatingResponseDto toResponse(Rating rating) {
        if(rating == null) {
            return null;
        }
        RatingResponseDto dto = modelMapper.map(rating, RatingResponseDto.class);
        dto.setUser(userMapper.toUserDetails(rating.getUser()));
        return dto;
    }

    public Rating fromCreateRequest(CreateRatingRequestDto createRatingRequestDto) {
        return modelMapper.map(createRatingRequestDto, Rating.class);
    }

}
