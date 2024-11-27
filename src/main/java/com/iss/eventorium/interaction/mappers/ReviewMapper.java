package com.iss.eventorium.interaction.mappers;

import com.iss.eventorium.interaction.dtos.CreateReviewRequestDto;
import com.iss.eventorium.interaction.dtos.ReviewResponseDto;
import com.iss.eventorium.interaction.models.Review;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    private static ModelMapper modelMapper;

    @Autowired
    public ReviewMapper(ModelMapper modelMapper) {
        ReviewMapper.modelMapper = modelMapper;
    }

    public static ReviewResponseDto toResponse(Review review) {
        return modelMapper.map(review, ReviewResponseDto.class);
    }

    public static Review fromCreateRequest(CreateReviewRequestDto createReviewRequestDto) {
        return modelMapper.map(createReviewRequestDto, Review.class);
    }

}
