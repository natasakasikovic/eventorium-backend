package com.iss.eventorium.solution.services;

import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.interaction.models.Rating;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.solution.models.Solution;
import com.iss.eventorium.solution.repositories.SolutionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SolutionService {

    private final SolutionRepository repository;

    public Solution find(Long id) {
        return repository.findById(id).orElseThrow( () -> new EntityNotFoundException("Solution not found"));
    }

    public void addRating(Solution solution, Rating rating) {
        solution.getRatings().add(rating);
        repository.save(solution);
    }

    public boolean existsCategory(Long categoryId) {
        return repository.existsByCategory_Id(categoryId);
    }

    public Solution findSolutionByCategory(Category category) {
        return repository.findByCategoryId(category.getId()).orElseThrow(
                () -> new EntityNotFoundException("Solution with category '" + category.getName() + "' not found"));
    }

    public void saveStatus(Solution solution, Status status) {
        solution.setStatus(status);
        repository.save(solution);
    }

    public void setCategory(Solution solution, Category category) {
        solution.setStatus(Status.ACCEPTED);
        solution.setCategory(category);
    }
}