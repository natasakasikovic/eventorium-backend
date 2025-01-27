package com.iss.eventorium.event.services;

import com.iss.eventorium.event.dtos.budget.BudgetItemRequestDto;
import com.iss.eventorium.event.dtos.budget.BudgetResponseDto;
import com.iss.eventorium.event.exceptions.AlreadyPurchasedException;
import com.iss.eventorium.event.exceptions.InsufficientFundsException;
import com.iss.eventorium.event.mappers.BudgetMapper;
import com.iss.eventorium.event.models.Budget;
import com.iss.eventorium.event.models.BudgetItem;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.repositories.EventRepository;
import com.iss.eventorium.solution.dtos.products.ProductResponseDto;
import com.iss.eventorium.solution.dtos.products.ProductReviewResponseDto;
import com.iss.eventorium.solution.dtos.products.ProductSummaryResponseDto;
import com.iss.eventorium.solution.mappers.ProductMapper;
import com.iss.eventorium.solution.models.Product;
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
    private final ProductRepository productRepository;

    public ProductResponseDto purchaseProduct(Long eventId, BudgetItemRequestDto dto) {
        Product product = productRepository.findById(dto.getItemId()).orElseThrow(
                () -> new EntityNotFoundException("Product with id " + dto.getItemId() + " not found")
        );
        if(product.getPrice() > dto.getPlannedAmount()) {
            throw new InsufficientFundsException("You do not have enough funds for this purchase!");
        }

        Event event = getEvent(eventId);
        updateBudget(event, BudgetMapper.fromRequest(dto, product));
        return ProductMapper.toResponse(product);
    }

    public List<ProductReviewResponseDto> getPurchasedProducts(Long eventId) {
        Event event = getEvent(eventId);
        Budget budget = event.getBudget();
        if(budget == null) {
            return new ArrayList<>();
        }

        return budget.getItems().stream()
                .filter(item -> item.getPurchased() != null)
                .map(item -> ProductMapper.toReviewResponse((Product) item.getSolution()))
                .toList();
    }

    public BudgetResponseDto getBudget(Long eventId) {
        Event event = getEvent(eventId);
        Budget budget = event.getBudget();
        if(budget == null) {
            budget = new Budget();
            event.setBudget(budget);
            eventRepository.save(event);
        }

        return BudgetMapper.toResponse(budget);
    }

    private void updateBudget(Event event, BudgetItem item) {
        Budget budget = event.getBudget();
        if(budget.getItems().stream().map(BudgetItem::getCategory).toList().contains(item.getCategory())) {
            throw new AlreadyPurchasedException("Product with the same category is already purchased!");
        }
        item.setPurchased(LocalDateTime.now());
        budget.addItem(item);
        eventRepository.save(event);
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("Event with id " + eventId + " not found")
        );
    }
}
