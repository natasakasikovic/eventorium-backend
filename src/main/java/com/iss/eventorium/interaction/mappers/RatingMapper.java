package com.iss.eventorium.interaction.mappers;

import com.iss.eventorium.interaction.dtos.CreateRatingRequestDto;
import com.iss.eventorium.interaction.dtos.RatingResponseDto;
import com.iss.eventorium.interaction.models.Rating;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RatingMapper {

    private static ModelMapper modelMapper;

    @Autowired
    public RatingMapper(ModelMapper modelMapper) {
        RatingMapper.modelMapper = modelMapper;
    }

    public static RatingResponseDto toResponse(Rating rating) {
        return modelMapper.map(rating, RatingResponseDto.class);
    }

    public static Rating fromCreateRequest(CreateRatingRequestDto createRatingRequestDto) {
        return modelMapper.map(createRatingRequestDto, Rating.class);
    }

}
