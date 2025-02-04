package com.iss.eventorium.solution.services;

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
}
