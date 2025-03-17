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
import com.iss.eventorium.event.services.AccountEventService;
import com.iss.eventorium.event.services.BudgetService;
import com.iss.eventorium.event.services.EventService;
import com.iss.eventorium.solution.dtos.products.ProductResponseDto;
import com.iss.eventorium.solution.dtos.products.SolutionReviewResponseDto;
import com.iss.eventorium.solution.mappers.ProductMapper;
import com.iss.eventorium.solution.mappers.SolutionMapper;
import com.iss.eventorium.solution.models.Product;
import com.iss.eventorium.solution.services.ProductService;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.services.AuthService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
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
    private AuthService authService;
    @MockBean
    private AccountEventService accountEventService;

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private BudgetMapper mapper;
    @MockBean
    private ProductMapper productMapper;
    @MockBean
    private SolutionMapper solutionMapper;


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
    void purchaseProduct_shouldUpdateBudgetWhenSufficientFunds(double price, double discount, double plannedAmount) {
        Product product = mockProduct(price, discount);
        Event event = mockEvent(new Budget());

        BudgetItem item = mock(BudgetItem.class);
        item.setSolution(product);
        when(mapper.fromRequest(any(), any(), any())).thenReturn(item);

        ProductResponseDto dto = mock(ProductResponseDto.class);
        when(productMapper.toResponse(any())).thenReturn(dto);
        BudgetItemRequestDto request = createRequest(plannedAmount);

        budgetService.purchaseProduct(1L, request);

        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void purchaseProduct_productNotFound_shouldThrowEntityNotFoundException() {
        BudgetItemRequestDto request = createRequest(100.0);

        when(productService.find(anyLong())).thenThrow(new EntityNotFoundException());

        assertThrows(EntityNotFoundException.class, () -> budgetService.purchaseProduct(1L, request));
    }

    @Test
    void purchaseProduct_eventNotFound_shouldThrowEntityNotFoundException() {
        mockProduct(100.0, 0.0);
        BudgetItemRequestDto request = createRequest(100.0);

        when(eventService.find(anyLong())).thenThrow(new EntityNotFoundException());

        assertThrows(EntityNotFoundException.class, () -> budgetService.purchaseProduct(1L, request));
    }

    @Test
    void getBudget_eventNotFound_shouldThrowEntityNotFoundException() {
        when(eventService.find(anyLong())).thenThrow(new EntityNotFoundException());
        assertThrows(EntityNotFoundException.class, () -> budgetService.getBudget(1L));
    }

    @Test
    void purchaseProduct_shouldThrowExceptionWhenCategoryAlreadyPurchased() {
        mockProduct(200.0, 10.0);

        Budget budget = mock(Budget.class);
        mockEvent(budget);

        BudgetItem existingItem = mock(BudgetItem.class);
        Category existingCategory = mock(Category.class);
        when(mapper.fromRequest(any(), any(), any())).thenReturn(existingItem);
        when(existingItem.getCategory()).thenReturn(existingCategory);
        when(budget.getItems()).thenReturn(List.of(existingItem));

        BudgetItemRequestDto request = createRequest(180.0);

        assertThrows(AlreadyPurchasedException.class, () -> budgetService.purchaseProduct(1L, request));
    }

    @Test
    void getBudget_shouldReturnExistingBudget() {
        mockEvent(new Budget());

        when(mapper.toResponse(any(Budget.class))).thenReturn(new BudgetResponseDto());

        BudgetResponseDto response = budgetService.getBudget(1L);
        assertNotNull(response);
    }

    @Test
    void getBudget_shouldCreateAndReturnNewBudgetIfNoneExists() {
        Event event = mockEvent(null);
        when(mapper.toResponse(any(Budget.class))).thenReturn(new BudgetResponseDto());

        BudgetResponseDto response = budgetService.getBudget(1L);

        assertNotNull(response);
        verify(eventRepository, times(1)).save(event);
    }

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.event.provider.BudgetProvider#provideEventsForBudget")
    void testGetAllBudgetItems(List<Event> events, long expectedSize) {
        User user = new User();
        when(authService.getCurrentUser()).thenReturn(user);
        when(accountEventService.findOrganizerEvents(user)).thenReturn(events);

        for (Event event : events) {
            if (event.getBudget() != null) {
                for (BudgetItem item : event.getBudget().getItems()) {
                    when(solutionMapper.toReviewResponse(user, item.getSolution(), item.getItemType()))
                            .thenReturn(new SolutionReviewResponseDto());
                }
            }
        }

        List<SolutionReviewResponseDto> result = budgetService.getAllBudgetItems();
        assertNotNull(result);
        assertEquals(expectedSize, result.size());
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
