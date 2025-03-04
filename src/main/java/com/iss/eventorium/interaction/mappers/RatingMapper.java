package com.iss.eventorium.interaction.mappers;

import com.iss.eventorium.interaction.dtos.ratings.CreateRatingRequestDto;
import com.iss.eventorium.interaction.dtos.ratings.RatingResponseDto;
import com.iss.eventorium.interaction.models.Rating;
import com.iss.eventorium.user.mappers.UserMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RatingMapper {

    private static ModelMapper modelMapper;

    @Autowired
    public RatingMapper(ModelMapper modelMapper) {
        RatingMapper.modelMapper = modelMapper;
    }

    public static RatingResponseDto toResponse(Rating rating) {
        if(rating == null) {
            return null;
        }
        RatingResponseDto dto = modelMapper.map(rating, RatingResponseDto.class);
        dto.setUser(UserMapper.toUserDetails(rating.getUser()));
        return dto;
    }

    public static Rating fromCreateRequest(CreateRatingRequestDto createRatingRequestDto) {
        return modelMapper.map(createRatingRequestDto, Rating.class);
    }

}
