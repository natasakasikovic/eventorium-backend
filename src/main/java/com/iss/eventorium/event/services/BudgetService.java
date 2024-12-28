package com.iss.eventorium.event.services;

import com.iss.eventorium.event.dtos.budget.BudgetItemRequestDto;
import com.iss.eventorium.event.dtos.budget.BudgetResponseDto;
import com.iss.eventorium.event.exceptions.InsufficientFundsException;
import com.iss.eventorium.event.mappers.BudgetMapper;
import com.iss.eventorium.event.models.Budget;
import com.iss.eventorium.event.models.BudgetItem;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.repositories.BudgetRepository;
import com.iss.eventorium.event.repositories.EventRepository;
import com.iss.eventorium.solution.dtos.products.ProductResponseDto;
import com.iss.eventorium.solution.dtos.products.ProductSummaryResponseDto;
import com.iss.eventorium.solution.mappers.ProductMapper;
import com.iss.eventorium.solution.models.Product;
import com.iss.eventorium.solution.models.Solution;
import com.iss.eventorium.solution.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final EventRepository eventRepository;
    private final BudgetRepository budgetRepository;
    private final ProductRepository productRepository;

    public ProductResponseDto purchaseProduct(Long eventId, BudgetItemRequestDto dto) {
        Product product = productRepository.findById(dto.getItemId()).orElseThrow(
                () -> new EntityNotFoundException("Event with id " + eventId + " not found")
        );
        if(product.getPrice() > dto.getPlannedAmount()) {
            throw new InsufficientFundsException("You do not have enough funds for this purchase!");
        }

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("Event with id " + eventId + " not found")
        );
        updateBudget(event, createBudgetItem(product, dto));
        return ProductMapper.toResponse(product);
    }

    private BudgetItem createBudgetItem(Solution solution, BudgetItemRequestDto dto) {
        BudgetItem item = BudgetMapper.fromRequest(dto);
        item.setCategory(solution.getCategory());
        item.setSolution(solution);
        return item;
    }

    private void updateBudget(Event event, BudgetItem item) {
        Budget budget =  event.getBudget();
        if(budget == null) {
            budget = new Budget();
            event.setBudget(budget);
        }
        item.setPurchased(LocalDateTime.now());
        budget.addItem(item);
        eventRepository.save(event);
    }

    public List<ProductSummaryResponseDto> getPurchasedProducts(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("Event with id " + eventId + " not found")
        );
        Budget budget = event.getBudget();
        if(budget == null) {
            return new ArrayList<>();
        }

        return budget.getItems().stream()
                .filter(item -> item.getPurchased() != null)
                .map(item -> ProductMapper.toSummaryResponse((Product) item.getSolution()))
                .toList();
    }

    public BudgetResponseDto getBudget(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("Event with id " + eventId + " not found")
        );
        Budget budget = event.getBudget();
        if(budget == null) {
            return null;
        }

        return BudgetMapper.toResponse(budget);
    }
}
