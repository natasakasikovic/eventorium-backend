package com.iss.eventorium.interaction.mappers;

import com.iss.eventorium.interaction.dtos.CreateReviewRequestDto;
import com.iss.eventorium.interaction.dtos.ReviewResponseDto;
import com.iss.eventorium.interaction.models.Review;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.user.models.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

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
        Review review = modelMapper.map(createReviewRequestDto, Review.class);
        review.setCreationDate(LocalDateTime.now());
        review.setStatus(Status.PENDING);
        return review;
    }

}
