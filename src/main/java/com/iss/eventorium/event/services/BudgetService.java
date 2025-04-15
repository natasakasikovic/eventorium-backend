package com.iss.eventorium.event.services;

import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.event.dtos.budget.BudgetItemRequestDto;
import com.iss.eventorium.event.dtos.budget.BudgetItemResponseDto;
import com.iss.eventorium.event.dtos.budget.BudgetResponseDto;
import com.iss.eventorium.event.exceptions.AlreadyPurchasedException;
import com.iss.eventorium.event.exceptions.InsufficientFundsException;
import com.iss.eventorium.event.mappers.BudgetMapper;
import com.iss.eventorium.event.models.Budget;
import com.iss.eventorium.event.models.BudgetItem;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.repositories.BudgetItemRepository;
import com.iss.eventorium.event.repositories.EventRepository;
import com.iss.eventorium.event.specifications.BudgetSpecification;
import com.iss.eventorium.solution.dtos.products.ProductResponseDto;
import com.iss.eventorium.solution.dtos.products.SolutionReviewResponseDto;
import com.iss.eventorium.solution.mappers.ProductMapper;
import com.iss.eventorium.solution.mappers.SolutionMapper;
import com.iss.eventorium.solution.models.Product;
import com.iss.eventorium.solution.models.Solution;
import com.iss.eventorium.solution.models.SolutionType;
import com.iss.eventorium.solution.services.ProductService;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final ProductService productService;
    private final EventService eventService;
    private final AuthService authService;

    private final EventRepository eventRepository;
    private final BudgetItemRepository budgetItemRepository;

    public ProductResponseDto purchaseProduct(Long eventId, BudgetItemRequestDto request) {
        Product product = productService.find(request.getItemId());
        double netPrice = calculateNetPrice(product);
        if(netPrice > request.getPlannedAmount()) {
            throw new InsufficientFundsException("You do not have enough funds for this purchase!");
        }

        Event event = eventService.find(eventId);
        updateBudget(event, BudgetMapper.fromRequest(request, product, SolutionType.PRODUCT));
        return ProductMapper.toResponse(product);
    }

    public BudgetResponseDto getBudget(Long eventId) {
        Event event = eventService.find(eventId);
        Budget budget = event.getBudget();
        if(budget == null) {
            budget = new Budget();
            event.setBudget(budget);
            eventRepository.save(event);
        }

        return BudgetMapper.toResponse(budget);
    }

    public List<SolutionReviewResponseDto> getAllBudgetItems() {
        User user = authService.getCurrentUser();
        Specification<BudgetItem> specification = BudgetSpecification.filterForOrganizer(user);
        return budgetItemRepository.findAll(specification).stream()
                .map(item -> SolutionMapper.toReviewResponse(user, item.getSolution(), item.getItemType()))
                .toList();
    }

    public List<BudgetItemResponseDto> getBudgetItems(Long eventId) {
        Event event = eventService.find(eventId);
        return event.getBudget().getItems().stream().map(BudgetMapper::toResponse).toList();
    }

    private void updateBudget(Event event, BudgetItem item) {
        Budget budget = event.getBudget();
        if(containsCategory(budget, item.getCategory())) {
            throw new AlreadyPurchasedException("Solution with the same category is already purchased!");
        }
        item.setPurchased(LocalDateTime.now());
        budget.addItem(item);
        eventRepository.save(event);
    }

    private boolean containsCategory(Budget budget, Category category) {
        return budget.getItems().stream().map(BudgetItem::getCategory).toList().contains(category);
    }

    private double calculateNetPrice(Solution solution) {
        return solution.getPrice() * (1 - solution.getDiscount() / 100);
    }
}
