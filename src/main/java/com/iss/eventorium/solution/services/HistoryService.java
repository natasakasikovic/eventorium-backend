package com.iss.eventorium.solution.services;

import com.iss.eventorium.solution.mappers.SolutionMapper;
import com.iss.eventorium.solution.models.Memento;
import com.iss.eventorium.solution.models.Solution;
import com.iss.eventorium.solution.repositories.MementoRepository;
import com.iss.eventorium.solution.specifications.MementoSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class HistoryService {

    private final MementoRepository mementoRepository;

    private final SolutionMapper solutionMapper;

    public void addMemento(Solution solution) {
        updateOldMemento(mementoRepository.findAll(
                MementoSpecification.filterBySolution(solution.getId())
        ));

        Memento memento = solutionMapper.toMemento(solution);
        memento.setId(0L);
        memento.setSolution(solution);
        memento.setValidFrom(LocalDateTime.now());
        mementoRepository.save(memento);
    }

    public Memento getValidSolution(Long id, LocalDateTime givenTime) {
        return findValidSolution(givenTime,  mementoRepository.findAll(MementoSpecification.hasSolution(id)));
    }

    private void updateOldMemento(List<Memento> mementos) {
        for (Memento old : mementos) {
            if (old.getValidTo() == null) {
                old.setValidTo(LocalDateTime.now());
                mementoRepository.save(old);
                break;
            }
        }
    }

    private Memento findValidSolution(LocalDateTime givenTime, List<Memento> mementos) {
        if (mementos.size() == 1) return mementos.get(0);

        return mementos.stream()
                .filter(m ->
                        (m.getValidFrom() == null || !m.getValidFrom().isAfter(givenTime)) &&
                                (m.getValidTo() == null || !m.getValidTo().isBefore(givenTime)))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Solution not found!"));
    }

}
