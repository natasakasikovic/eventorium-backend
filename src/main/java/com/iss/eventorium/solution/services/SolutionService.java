package com.iss.eventorium.solution.services;

import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.interaction.models.Rating;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.solution.models.Solution;
import com.iss.eventorium.solution.repositories.SolutionRepository;
import com.iss.eventorium.solution.specifications.SolutionSpecification;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.services.AuthService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolutionService {

    private final SolutionRepository repository;
    private final AuthService authService;

    public List<Solution> findSuggestions(Category category, double maxPrice, LocalDate eventDate) {
        User user = authService.getCurrentUser();
        Specification<Solution> specification = SolutionSpecification.filterSuggestions(user, category, maxPrice, eventDate);
        return repository.findAll(specification);
    }

    public Solution find(Long id) {
        return repository.findById(id).orElseThrow( () -> new EntityNotFoundException("Solution not found"));
    }

    public void addRating(Solution solution, Rating rating) {
        solution.getRatings().add(rating);
        repository.save(solution);
    }

    public boolean existsCategory(Long categoryId) {
        return repository.count(SolutionSpecification.hasCategory(categoryId)) > 0;
    }

    public Solution findSolutionByCategory(Category category) {
        return repository.findOne(SolutionSpecification.hasCategory(category.getId()))
                .orElseThrow(() -> new EntityNotFoundException("Solution with category '" + category.getName() + "' not found"));
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