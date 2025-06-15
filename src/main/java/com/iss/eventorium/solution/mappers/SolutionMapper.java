package com.iss.eventorium.solution.mappers;

import com.iss.eventorium.interaction.mappers.RatingMapper;
import com.iss.eventorium.interaction.models.Rating;
import com.iss.eventorium.solution.dtos.products.SolutionReviewResponseDto;
import com.iss.eventorium.solution.models.Memento;
import com.iss.eventorium.solution.models.Solution;
import com.iss.eventorium.solution.models.SolutionType;
import com.iss.eventorium.user.models.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SolutionMapper {

    private final ModelMapper modelMapper;
    private final RatingMapper ratingMapper;

    public SolutionReviewResponseDto toReviewResponse(User user, Solution solution, SolutionType itemType) {
        SolutionReviewResponseDto dto = modelMapper.map(solution, SolutionReviewResponseDto.class);
        Rating solutionRating = solution.getRatings().stream()
                .filter(rating -> rating.getRater().equals(user))
                .findFirst()
                .orElse(null);
        dto.setRating(ratingMapper.toResponse(solutionRating));
        dto.setType(itemType);
        return dto;
    }

    public Memento toMemento(Solution solution) {
        return modelMapper.map(solution, Memento.class);
    }
}