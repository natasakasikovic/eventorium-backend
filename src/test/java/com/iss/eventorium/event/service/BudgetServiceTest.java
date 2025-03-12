package com.iss.eventorium.event.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.event.dtos.budget.BudgetItemRequestDto;
import com.iss.eventorium.event.dtos.budget.BudgetResponseDto;
import com.iss.eventorium.event.exceptions.AlreadyPurchasedException;
import com.iss.eventorium.event.exceptions.InsufficientFundsException;
import com.iss.eventorium.event.mappers.BudgetMapper;
import com.iss.eventorium.event.models.Budget;
import com.iss.eventorium.event.models.BudgetItem;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.repositories.EventRepository;
import com.iss.eventorium.event.services.BudgetService;
import com.iss.eventorium.event.services.EventService;
import com.iss.eventorium.solution.models.Product;
import com.iss.eventorium.solution.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BudgetServiceTest {

    @MockBean
    private ProductService productService;
    @MockBean
    private EventService eventService;
    @MockBean
    private EventRepository eventRepository;
    @MockBean
    private BudgetMapper mapper;

    @Autowired
    private BudgetService budgetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @CsvSource({
            "200, 10, 179.99",
            "150, 5, 140",
            "300, 20, 239",
            "5000, 0, 4500",
            "1000, 50, 499",
            "500, 0, 400",
            "200, 0, 150",
            "900, 99, 0"
    })
    void purchaseProduct_shouldThrowExceptionWhenInsufficientFunds(double price, double discount, double plannedAmount) {
        Product product = mock(Product.class);

        when(product.getPrice()).thenReturn(price);
        when(product.getDiscount()).thenReturn(discount);
        when(productService.find(anyLong())).thenReturn(product);

        BudgetItemRequestDto request = new BudgetItemRequestDto();
        request.setItemId(1L);
        request.setPlannedAmount(plannedAmount);

        assertThrows(InsufficientFundsException.class, () -> budgetService.purchaseProduct(1L, request));
    }

    @ParameterizedTest
    @CsvSource({
            "200.0, 10.0, 180.0",
            "500.0, 20.0, 400.0",
            "1000.0, 0.0, 1000.0",
            "100.0, 0.0, 100.0",
            "50.0, 0.0, 50.0"
    })
    void purchaseProduct_shouldUpdateBudgetWhenSufficientFunds(double price, double discount, double plannedAmount) {
        Product product = mock(Product.class);
        when(product.getPrice()).thenReturn(price);
        when(product.getDiscount()).thenReturn(discount);
        when(productService.find(anyLong())).thenReturn(product);

        Event event = mock(Event.class);
        Budget budget = new Budget();

        BudgetItem item = mock(BudgetItem.class);
        item.setSolution(product);

        when(eventService.find(anyLong())).thenReturn(event);
        when(event.getBudget()).thenReturn(budget);
        when(mapper.fromRequest(any(), any(), any())).thenReturn(item);

        BudgetItemRequestDto request = new BudgetItemRequestDto();
        request.setItemId(1L);
        request.setPlannedAmount(plannedAmount);

        budgetService.purchaseProduct(1L, request);

        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void getBudget_shouldReturnExistingBudget() {
        Event event = mock(Event.class);
        Budget budget = new Budget();
        when(eventService.find(anyLong())).thenReturn(event);
        when(event.getBudget()).thenReturn(budget);
        when(mapper.toResponse(any(Budget.class))).thenReturn(new BudgetResponseDto());

        BudgetResponseDto response = budgetService.getBudget(1L);
        assertNotNull(response);
    }

    @Test
    void getBudget_shouldCreateAndReturnNewBudgetIfNoneExists() {
        Event event = mock(Event.class);
        when(eventService.find(anyLong())).thenReturn(event);
        when(event.getBudget()).thenReturn(null);
        when(mapper.toResponse(any(Budget.class))).thenReturn(new BudgetResponseDto());

        BudgetResponseDto response = budgetService.getBudget(1L);
        assertNotNull(response);

        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void purchaseProduct_shouldThrowExceptionWhenCategoryAlreadyPurchased() {
        Product product = mock(Product.class);
        when(product.getPrice()).thenReturn(200.0);
        when(product.getDiscount()).thenReturn(10.0);
        when(productService.find(anyLong())).thenReturn(product);

        Event event = mock(Event.class);
        Budget budget = mock(Budget.class);
        when(eventService.find(anyLong())).thenReturn(event);
        when(event.getBudget()).thenReturn(budget);

        BudgetItem existingItem = mock(BudgetItem.class);
        Category existingCategory = mock(Category.class);
        when(mapper.fromRequest(any(), any(), any())).thenReturn(existingItem);
        when(existingItem.getCategory()).thenReturn(existingCategory);
        when(budget.getItems()).thenReturn(List.of(existingItem));

        BudgetItemRequestDto request = new BudgetItemRequestDto();
        request.setItemId(1L);
        request.setPlannedAmount(180.0);

        assertThrows(AlreadyPurchasedException.class, () -> budgetService.purchaseProduct(1L, request));
    }

}
