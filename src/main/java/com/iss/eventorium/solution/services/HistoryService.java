package com.iss.eventorium.solution.services;

import com.iss.eventorium.solution.mappers.ServiceMapper;
import com.iss.eventorium.solution.models.Memento;
import com.iss.eventorium.solution.models.Product;
import com.iss.eventorium.solution.models.Service;
import com.iss.eventorium.solution.models.SolutionHistory;
import com.iss.eventorium.solution.repositories.HistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;

    private static final Long HISTORY_ID = 1L;

    public SolutionHistory getSolutionHistory() {
        return historyRepository.findById(HISTORY_ID)
                .orElseThrow(() -> new EntityNotFoundException("SolutionHistory not found with ID " + HISTORY_ID));
    }

    public void addServiceMemento(Service service) {
        Memento memento = ServiceMapper.toMemento(service);
        memento.setId(null);
        memento.setSolutionId(service.getId());
        memento.setValidFrom(LocalDateTime.now());
        SolutionHistory solutionHistory = getSolutionHistory();

        solutionHistory.addServiceMemento(memento);
        historyRepository.save(solutionHistory);
    }

    public Memento getValidService(LocalDateTime givenTime) {
        List<Memento> mementos = getSolutionHistory().getServiceHistories();
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
}
