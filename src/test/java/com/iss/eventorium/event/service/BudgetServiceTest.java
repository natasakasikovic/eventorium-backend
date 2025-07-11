package com.iss.eventorium.event.service;

import com.iss.eventorium.event.dtos.budget.BudgetItemRequestDto;
import com.iss.eventorium.event.dtos.budget.BudgetResponseDto;
import com.iss.eventorium.event.mappers.BudgetMapper;
import com.iss.eventorium.event.models.Budget;
import com.iss.eventorium.event.models.BudgetItem;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.repositories.EventRepository;
import com.iss.eventorium.event.services.BudgetService;
import com.iss.eventorium.event.services.EventService;
import com.iss.eventorium.shared.exceptions.InsufficientFundsException;
import com.iss.eventorium.solution.dtos.products.ProductResponseDto;
import com.iss.eventorium.solution.mappers.ProductMapper;
import com.iss.eventorium.solution.models.Product;
import com.iss.eventorium.solution.services.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private ProductService productService;
    @Mock
    private EventService eventService;
    @Mock
    private EventRepository eventRepository;

    @Mock
    private BudgetMapper mapper;
    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private BudgetService budgetService;

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
    void givenInsufficientFunds_whenPurchaseProduct_thenThrowInsufficientFundsException(double price, double discount, double plannedAmount) {
        mockProduct(price, discount);
        BudgetItemRequestDto request = createRequest(plannedAmount);

        assertThrows(InsufficientFundsException.class, () -> budgetService.purchaseProduct(1L, request));
    }

    @ParameterizedTest
    @CsvSource({
            "200.0, 10.0, 180.0",
            "500.0, 20.0, 400.0",
            "1000.0, 0.0, 1000.0",
            "100.0, 10.0, 100.0",
            "50.0, 50.0, 25.0",
            "0.0, 0.0, 100.0"
    })
    void givenSufficientBudget_whenPurchasingProduct_thenEventIsSaved(double price, double discount, double plannedAmount) {
        Product product = mockProduct(price, discount);
        Event event = mockEvent(new Budget());

        BudgetItem item = mock(BudgetItem.class);
        item.setSolution(product);
        when(mapper.fromRequest(any(), any())).thenReturn(item);

        ProductResponseDto dto = mock(ProductResponseDto.class);
        when(productMapper.toResponse(any())).thenReturn(dto);
        BudgetItemRequestDto request = createRequest(plannedAmount);

        budgetService.purchaseProduct(1L, request);

        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void givenProductDoesNotExist_whenPurchaseProduct_thenThrowEntityNotFoundException() {
        BudgetItemRequestDto request = createRequest(100.0);

        when(productService.find(anyLong())).thenThrow(new EntityNotFoundException());

        assertThrows(EntityNotFoundException.class, () -> budgetService.purchaseProduct(1L, request));
    }

    @Test
    void givenEventDoesNotExist_whenPurchaseProduct_thenThrowEntityNotFoundException() {
        mockProduct(100.0, 0.0);
        BudgetItemRequestDto request = createRequest(100.0);

        when(eventService.find(anyLong())).thenThrow(new EntityNotFoundException());

        assertThrows(EntityNotFoundException.class, () -> budgetService.purchaseProduct(1L, request));
    }

    @Test
    void givenEventDoesNotExist_whenGetBudget_thenThrowEntityNotFoundException() {
        when(eventService.find(anyLong())).thenThrow(new EntityNotFoundException());
        assertThrows(EntityNotFoundException.class, () -> budgetService.getBudget(1L));
    }

    @Test
    void givenExistingBudget_whenGetBudget_thenReturnBudgetResponse() {
        mockEvent(new Budget());

        when(mapper.toResponse(any(Budget.class))).thenReturn(new BudgetResponseDto());

        BudgetResponseDto response = budgetService.getBudget(1L);
        assertNotNull(response);
    }

    private Product mockProduct(double price, double discount) {
        Product product = mock(Product.class);
        when(product.getPrice()).thenReturn(price);
        when(product.getDiscount()).thenReturn(discount);
        when(productService.find(anyLong())).thenReturn(product);
        return product;
    }

    private BudgetItemRequestDto createRequest(double plannedAmount) {
        return BudgetItemRequestDto.builder().itemId(1L).plannedAmount(plannedAmount).build();
    }

    private Event mockEvent(Budget budget) {
        Event event = mock(Event.class);
        when(eventService.find(anyLong())).thenReturn(event);
        when(event.getBudget()).thenReturn(budget);
        return event;
    }
}
