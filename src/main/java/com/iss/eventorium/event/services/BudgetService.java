package com.iss.eventorium.event.services;

import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.category.services.CategoryService;
import com.iss.eventorium.event.dtos.budget.BudgetItemRequestDto;
import com.iss.eventorium.event.dtos.budget.BudgetItemResponseDto;
import com.iss.eventorium.event.dtos.budget.BudgetResponseDto;
import com.iss.eventorium.event.dtos.budget.BudgetSuggestionResponseDto;
import com.iss.eventorium.event.exceptions.AlreadyPurchasedException;
import com.iss.eventorium.shared.exceptions.InsufficientFundsException;
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
import com.iss.eventorium.solution.models.*;
import com.iss.eventorium.solution.services.HistoryService;
import com.iss.eventorium.solution.services.ProductService;
import com.iss.eventorium.solution.services.SolutionService;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.services.AuthService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Slf4j
public class BudgetService {

    private final SolutionService solutionService;
    private final ProductService productService;
    private final EventService eventService;
    private final AuthService authService;
    private final CategoryService categoryService;
    private final HistoryService historyService;

    private final EventRepository eventRepository;
    private final BudgetItemRepository budgetItemRepository;

    private final BudgetMapper mapper;
    private final ProductMapper productMapper;
    private final SolutionMapper solutionMapper;

    public ProductResponseDto purchaseProduct(Long eventId, BudgetItemRequestDto request) {
        Product product = productService.find(request.getItemId());
        double netPrice = calculateNetPrice(product);
        if(netPrice > request.getPlannedAmount()) {
            throw new InsufficientFundsException("You do not have enough funds for this purchase!");
        }

        Event event = eventService.find(eventId);
        updateBudget(event, mapper.fromRequest(request, product, SolutionType.PRODUCT));
        return productMapper.toResponse(product);
    }

    public List<BudgetSuggestionResponseDto> getBudgetSuggestions(Long eventId, Long categoryId, double price) {
        Category category = categoryService.find(categoryId);
        Event event = eventService.find(eventId);
        List<Solution> solutions = solutionService.findSuggestions(category, price, event.getDate());
        return solutions.stream().map(mapper::toSuggestionResponse).toList();
    }

    public BudgetResponseDto getBudget(Long eventId) {
        Event event = eventService.find(eventId);
        Budget budget = event.getBudget();
        return mapper.toResponse(budget);
    }

    public List<SolutionReviewResponseDto> getAllBudgetItems() {
        User user = authService.getCurrentUser();
        Specification<BudgetItem> specification = BudgetSpecification.filterForOrganizer(user);
        return budgetItemRepository.findAll(specification).stream()
                .map(item -> solutionMapper.toReviewResponse(user, item.getSolution(), item.getItemType()))
                .toList();
    }

    public void addReservationAsBudgetItem(Reservation reservation, double plannedAmount) {
        Budget budget = reservation.getEvent().getBudget();
        Service service = reservation.getService();
        budget.addItem(BudgetItem.builder()
                .itemType(SolutionType.SERVICE)
                .plannedAmount(plannedAmount)
                .processedAt(service.getType() == ReservationType.AUTOMATIC ? LocalDateTime.now() : null)
                .solution(service)
                .category(service.getCategory())
                .build());

        eventRepository.save(reservation.getEvent());
    }

    public void markAsReserved(Reservation reservation) {
        Event event = reservation.getEvent();
        Budget budget = event.getBudget();
        Long serviceId = reservation.getService().getId();

        BudgetItem budgetItem = budget.getItems().stream()
                .filter(item -> Objects.equals(item.getSolution().getId(), serviceId)
                        && item.getItemType() == SolutionType.SERVICE)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Matching budget item not found."));

        budgetItem.setProcessedAt(LocalDateTime.now());
        budgetItemRepository.save(budgetItem);
    }

    public List<BudgetItemResponseDto> getBudgetItems(Long eventId) {
        Event event = eventService.find(eventId);
        List<BudgetItem> items = event.getBudget().getItems();
        for(BudgetItem item : items) {
            if(item.getProcessedAt() != null) {
                Memento memento = historyService.getValidSolution(item.getItemType(), item.getProcessedAt());
                item.getSolution().restore(memento);
            }
        }
        return items.stream().map(mapper::toResponse).toList();
    }

    private void updateBudget(Event event, BudgetItem item) {
        Budget budget = event.getBudget();
        if(containsCategory(budget, item.getCategory())) {
            throw new AlreadyPurchasedException("Solution with the same category is already purchased!");
        }
        item.setProcessedAt(LocalDateTime.now());
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
