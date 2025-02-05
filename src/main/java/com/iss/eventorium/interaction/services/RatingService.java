package com.iss.eventorium.interaction.services;

import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.repositories.EventRepository;
import com.iss.eventorium.event.services.EventService;
import com.iss.eventorium.interaction.dtos.ratings.CreateRatingRequestDto;
import com.iss.eventorium.interaction.dtos.ratings.RatingResponseDto;
import com.iss.eventorium.interaction.mappers.RatingMapper;
import com.iss.eventorium.interaction.models.Rating;
import com.iss.eventorium.interaction.repositories.RatingRepository;
import com.iss.eventorium.solution.models.Solution;
import com.iss.eventorium.solution.repositories.SolutionRepository;
import com.iss.eventorium.solution.services.SolutionService;
import com.iss.eventorium.user.services.AuthService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.iss.eventorium.interaction.mappers.RatingMapper.toResponse;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final AuthService authService;
    private final EventService eventService;
    private final SolutionService solutionService;

    private final RatingRepository ratingRepository;

    public RatingResponseDto createSolutionRating(Long solutionId, CreateRatingRequestDto request) {
        Rating rating = RatingMapper.fromCreateRequest(request);
        rating.setUser(authService.getCurrentUser());
        solutionService.addRating(solutionId, rating);
        return toResponse(rating);
    }

    public RatingResponseDto createEventRating(Long eventId, CreateRatingRequestDto request) {
        Rating rating = RatingMapper.fromCreateRequest(request);
        rating.setUser(authService.getCurrentUser());
        eventService.addRating(eventId, rating);
        return toResponse(rating);
    }

    public Rating find(Long id) {
        return ratingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Rating not found")
        );
    }
}
