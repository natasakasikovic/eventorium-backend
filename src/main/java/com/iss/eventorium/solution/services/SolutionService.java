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

    private final SolutionRepository solutionRepository;

    public Solution find(Long id) {
        return solutionRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Solution not found")
        );
    }

    public void addRating(Long id, Rating rating) {
        Solution solution = find(id);
        solution.addRating(rating);
        solutionRepository.save(solution);
    }

    public boolean existsCategory(Long categoryId) {
        return solutionRepository.existsByCategory_Id(categoryId);
    }

    public Solution findSolutionByCategoryId(Category category) {
        return solutionRepository.findByCategoryId(category.getId()).orElseThrow(
                () -> new EntityNotFoundException("Solution with category '" + category.getName() + "' not found"));
    }

    public void saveStatus(Solution solution, Status status) {
        solution.setStatus(status);
        solutionRepository.save(solution);
    }

    public void saveCategory(Solution solution, Category category) {
        solution.setStatus(Status.ACCEPTED);
        solution.setCategory(category);
    }
}
