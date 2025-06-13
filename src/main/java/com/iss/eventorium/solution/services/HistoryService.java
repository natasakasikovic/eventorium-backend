package com.iss.eventorium.solution.services;

import com.iss.eventorium.solution.mappers.ProductMapper;
import com.iss.eventorium.solution.mappers.ServiceMapper;
import com.iss.eventorium.solution.models.*;
import com.iss.eventorium.solution.repositories.HistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;

    private static final Long HISTORY_ID = 1L;

    private final ProductMapper productMapper;
    private final ServiceMapper serviceMapper;

    public void addServiceMemento(Service service) {
        Memento memento = serviceMapper.toMemento(service);
        memento.setId(null);
        memento.setSolutionId(service.getId());
        memento.setValidFrom(LocalDateTime.now());
        SolutionHistory solutionHistory = getSolutionHistory();

        solutionHistory.addServiceMemento(memento);
        historyRepository.save(solutionHistory);
    }

    public Memento getValidSolution(SolutionType type, LocalDateTime validFrom) {
        if(type == SolutionType.SERVICE)
            return getValidService(validFrom);
        else
            return getValidProduct(validFrom);

    }

    public void addProductMemento(Product product) {
        Memento memento = productMapper.toMemento(product);
        memento.setId(null);
        memento.setSolutionId(product.getId());
        memento.setValidFrom(LocalDateTime.now());
        SolutionHistory solutionHistory = getSolutionHistory();

        solutionHistory.addProductMemento(memento);
        historyRepository.save(solutionHistory);
    }

    private Memento getValidService(LocalDateTime givenTime) {
        List<Memento> mementos = getSolutionHistory().getServiceHistories();
        return findValidSolution(givenTime, mementos);
    }

    private Memento getValidProduct(LocalDateTime givenTime) {
        List<Memento> mementos = getSolutionHistory().getProductHistories();
        return findValidSolution(givenTime, mementos);
    }

    private Memento findValidSolution(LocalDateTime givenTime, List<Memento> mementos) {
        if(mementos.size() == 1) {
            return mementos.get(0);
        }
        return mementos.stream()
                .filter(memento ->
                        (memento.getValidFrom() == null || !memento.getValidFrom().isAfter(givenTime)) &&
                                (memento.getValidTo() == null || !memento.getValidTo().isBefore(givenTime))
                )
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Memento not found! This should never happen"));
    }

    private SolutionHistory getSolutionHistory() {
        return historyRepository.findById(HISTORY_ID)
                .orElseThrow(() -> new EntityNotFoundException("SolutionHistory not found with ID " + HISTORY_ID));
    }
}
