package com.iss.eventorium.solution.mappers;

import com.iss.eventorium.interaction.mappers.RatingMapper;
import com.iss.eventorium.interaction.models.Rating;
import com.iss.eventorium.solution.dtos.products.SolutionReviewResponseDto;
import com.iss.eventorium.solution.models.Solution;
import com.iss.eventorium.solution.models.SolutionType;
import com.iss.eventorium.user.models.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SolutionMapper {

    private static ModelMapper modelMapper;

    @Autowired
    public SolutionMapper(ModelMapper modelMapper) {
        SolutionMapper.modelMapper = modelMapper;
    }

    public static SolutionReviewResponseDto toReviewResponse(User user, Solution solution, SolutionType itemType) {
        SolutionReviewResponseDto dto = modelMapper.map(solution, SolutionReviewResponseDto.class);
        Rating solutionRating = solution.getRatings().stream()
                .filter(rating -> rating.getUser().equals(user))
                .findFirst()
                .orElse(null);
        dto.setRating(RatingMapper.toResponse(solutionRating));
        dto.setType(itemType);
        return dto;
    }
}
