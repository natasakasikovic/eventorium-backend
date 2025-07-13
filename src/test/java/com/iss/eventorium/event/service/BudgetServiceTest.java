package com.iss.eventorium.event.service;

import com.iss.eventorium.event.dtos.budget.BudgetItemRequestDto;
import com.iss.eventorium.event.dtos.budget.BudgetItemResponseDto;
import com.iss.eventorium.event.dtos.budget.BudgetResponseDto;
import com.iss.eventorium.event.dtos.budget.UpdateBudgetItemRequestDto;
import com.iss.eventorium.event.exceptions.AlreadyProcessedException;
import com.iss.eventorium.event.mappers.BudgetMapper;
import com.iss.eventorium.event.models.Budget;
import com.iss.eventorium.event.models.BudgetItem;
import com.iss.eventorium.event.models.BudgetItemStatus;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.repositories.BudgetItemRepository;
import com.iss.eventorium.event.repositories.EventRepository;
import com.iss.eventorium.event.services.BudgetService;
import com.iss.eventorium.event.services.EventService;
import com.iss.eventorium.shared.exceptions.InsufficientFundsException;
import com.iss.eventorium.solution.dtos.products.ProductResponseDto;
import com.iss.eventorium.solution.mappers.ProductMapper;
import com.iss.eventorium.solution.models.Product;
import com.iss.eventorium.solution.models.Reservation;
import com.iss.eventorium.solution.models.Service;
import com.iss.eventorium.solution.models.SolutionType;
import com.iss.eventorium.solution.services.ProductService;
import com.iss.eventorium.solution.services.SolutionService;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.services.AuthService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    private static final Long DEFAULT_EVENT_ID = 1L;
    private static final Long DEFAULT_BUDGET_ITEM_ID = 1L;

    @Mock
    private SolutionService solutionService;
    @Mock
    private ProductService productService;
    @Mock
    private EventService eventService;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private BudgetItemRepository budgetItemRepository;
    @Mock
    private AuthService authService;
    @Mock
    private BudgetMapper mapper;
    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private BudgetService budgetService;

    private User organizer;

    @BeforeEach
    void setup() {
        organizer = User.builder().id(1L).build();
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
    @Tag("purchase-product")
    void givenInsufficientFunds_whenPurchaseProduct_thenThrowInsufficientFundsException(double price, double discount, double plannedAmount) {
        mockProduct(price, discount);
        BudgetItemRequestDto request = createRequest(plannedAmount);

        InsufficientFundsException exception = assertThrows(
                InsufficientFundsException.class,
                () -> budgetService.purchaseProduct(DEFAULT_EVENT_ID, request)
        );
        assertEquals("You do not have enough funds for this purchase!", exception.getMessage());
    }

    @Test
    @Tag("purchase-product")
    void givenUserIsNotEventOwner_whenPurchaseProduct_thenThrowsOwnershipRequiredException() {
        User user = User.builder().id(999L).build();
        Event event = mock(Event.class);
        when(event.getOrganizer()).thenReturn(user);
        when(authService.getCurrentUser()).thenReturn(organizer);

        budgetService.purchaseProduct(DEFAULT_EVENT_ID, createRequest(100.0));
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
    @Tag("purchase-product")
    void givenSufficientBudget_whenPurchasingProduct_thenEventIsSaved(double price, double discount, double plannedAmount) {
        Product product = mockProduct(price, discount);
        Event event = mockEvent(new Budget());

        BudgetItem item = mock(BudgetItem.class);
        item.setSolution(product);
        when(mapper.fromRequest(any(), any())).thenReturn(item);

        ProductResponseDto dto = mock(ProductResponseDto.class);
        when(productMapper.toResponse(any())).thenReturn(dto);
        BudgetItemRequestDto request = createRequest(plannedAmount);

        budgetService.purchaseProduct(DEFAULT_EVENT_ID, request);

        verify(eventRepository, times(1)).save(event);
    }

    @Test
    @Tag("purchase-product")
    void givenProductDoesNotExist_whenPurchaseProduct_thenThrowEntityNotFoundException() {
        BudgetItemRequestDto request = createRequest(100.0);

        when(productService.find(anyLong())).thenThrow(new EntityNotFoundException("Product not found"));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> budgetService.purchaseProduct(DEFAULT_EVENT_ID, request)
        );

        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    void givenEventDoesNotExist_whenPurchaseProduct_thenThrowEntityNotFoundException() {
        mockProduct(100.0, 0.0);
        BudgetItemRequestDto request = createRequest(100.0);

        when(eventService.find(anyLong())).thenThrow(new EntityNotFoundException("Event not found"));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> budgetService.purchaseProduct(1L, request),
                "Event not found"
        );
        assertEquals("Event not found", exception.getMessage());
    }

    @Test
    @Tag("purchase-product")
    void givenProcessedProduct_whenPurchaseProduct_thenThrowAlreadyProcessedException() {
        BudgetItemRequestDto request = createRequest(100.0);

        Product product = new Product();
        product.setId(1L);
        product.setPrice(100.0);
        product.setDiscount(50.0);
        when(productService.find(anyLong())).thenReturn(product);

        BudgetItem processedItem = new BudgetItem();
        processedItem.setStatus(BudgetItemStatus.PROCESSED);
        processedItem.setSolution(product);
        processedItem.setProcessedAt(LocalDateTime.now());
        when(mapper.fromRequest(request, product)).thenReturn(processedItem);

        Budget budget = new Budget();
        budget.setItems(List.of(processedItem));
        mockEvent(budget);

        AlreadyProcessedException exception = assertThrows(
                AlreadyProcessedException.class,
                () -> budgetService.purchaseProduct(DEFAULT_EVENT_ID, request),
                "Solution is already processed"
        );
        assertEquals("Solution is already processed", exception.getMessage());
    }

    @Test
    @Tag("purchase-product")
    void givenNotProcessedProduct_whenPurchaseProduct_thenReturnPurchasedProduct() {
        BudgetItemRequestDto request = createRequest(200.0);

        Product product = new Product();
        product.setId(1L);
        product.setPrice(100.0);
        product.setDiscount(50.0);
        when(productService.find(anyLong())).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(ProductResponseDto.builder().id(1L).build());

        BudgetItem item = new BudgetItem();
        item.setStatus(BudgetItemStatus.PROCESSED);
        item.setSolution(product);
        item.setProcessedAt(null);
        when(mapper.fromRequest(request, product)).thenReturn(item);

        Budget budget = new Budget();
        budget.setItems(List.of(item));
        mockEvent(budget);

        ProductResponseDto response = budgetService.purchaseProduct(DEFAULT_EVENT_ID, request);
        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @Tag("get-budget")
    void givenEventDoesNotExist_whenGetBudget_thenThrowEntityNotFoundException() {
        when(eventService.find(anyLong())).thenThrow(new EntityNotFoundException("Event not found"));
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> budgetService.getBudget(DEFAULT_EVENT_ID)
        );
        assertEquals("Event not found", exception.getMessage());
    }

    @Test
    @Tag("get-budget")
    void givenExistingBudget_whenGetBudget_thenReturnBudgetResponse() {
        mockEvent(new Budget());

        when(mapper.toResponse(any(Budget.class))).thenReturn(new BudgetResponseDto());

        BudgetResponseDto response = budgetService.getBudget(DEFAULT_EVENT_ID);
        assertNotNull(response);
    }

    @Test
    @Tag("update-budget-item")
    void givenValidBudgetItem_whenUpdateBudgetItem_thenSaveBudgetWithUpdatedItem() {
        UpdateBudgetItemRequestDto request = new UpdateBudgetItemRequestDto(200.0);
        BudgetItem item = mockProductBudgetItemProcessedAt(null);
        Budget budget = new Budget();
        budget.addItem(item);

        mockEvent(budget);
        when(mapper.toResponse(item)).thenReturn(new BudgetItemResponseDto());

        budgetService.updateBudgetItem(DEFAULT_EVENT_ID, DEFAULT_BUDGET_ITEM_ID, request);

        verify(item).setPlannedAmount(200.0);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @Tag("update-budget-item")
    void givenProcessedBudgetItem_whenUpdateBudgetItem_thenThrowAlreadyProcessedException() {
        UpdateBudgetItemRequestDto request = new UpdateBudgetItemRequestDto(200.0);
        BudgetItem item = mockProductBudgetItemProcessedAt(LocalDateTime.now());
        Budget budget = new Budget();
        budget.addItem(item);

        mockEvent(budget);

        AlreadyProcessedException exception = assertThrows(
                AlreadyProcessedException.class,
                () -> budgetService.updateBudgetItem(DEFAULT_EVENT_ID, DEFAULT_BUDGET_ITEM_ID, request)
        );

        assertEquals("Solution is already processed", exception.getMessage());
    }

    @Test
    @Tag("update-budget-item")
    void givenInsufficientBudgetItem_whenUpdateBudgetItem_thenThrowInsufficientBudgetException() {
        UpdateBudgetItemRequestDto request = new UpdateBudgetItemRequestDto(0.0);
        BudgetItem item = mockProductBudgetItemProcessedAt(null);
        Budget budget = new Budget();
        budget.addItem(item);
        mockEvent(budget);

        InsufficientFundsException exception = assertThrows(
                InsufficientFundsException.class,
                () -> budgetService.updateBudgetItem(DEFAULT_EVENT_ID, DEFAULT_BUDGET_ITEM_ID, request)
        );

        assertEquals("You do not have enough funds for this purchase/reservation!", exception.getMessage());
    }

    @Test
    @Tag("update-budget-item")
    void givenNonExistentEvent_whenUpdateBudgetItem_thenThrowEntityNotFoundException() {
        UpdateBudgetItemRequestDto request = new UpdateBudgetItemRequestDto(200.0);
        when(eventService.find(anyLong())).thenThrow(new EntityNotFoundException("Event not found"));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> budgetService.updateBudgetItem(DEFAULT_EVENT_ID, DEFAULT_BUDGET_ITEM_ID, request)
        );

        assertEquals("Event not found", exception.getMessage());
    }

    @Test
    @Tag("update-budget-item")
    void givenNonExistentBudgetItem_whenUpdateBudgetItem_thenThrowEntityNotFoundException() {
        UpdateBudgetItemRequestDto request = new UpdateBudgetItemRequestDto(200.0);
        mockEvent(new Budget());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> budgetService.updateBudgetItem(DEFAULT_EVENT_ID, DEFAULT_BUDGET_ITEM_ID, request),
                "Budget item not found."
        );
        assertEquals("Budget item not found.", exception.getMessage());
    }

    @Test
    @Tag("delete-budget-item")
    void givenPlannedBudgetItem_whenDeleteBudgetItem_thenItemIsRemovedAndEventSaved() {
        BudgetItem item = mock(BudgetItem.class);
        when(item.getId()).thenReturn(DEFAULT_BUDGET_ITEM_ID);
        when(item.getStatus()).thenReturn(BudgetItemStatus.PLANNED);
        Budget budget = mock(Budget.class);
        when(budget.getItems()).thenReturn(List.of(item));
        budget.addItem(item);
        Event event = mockEvent(budget);

        budgetService.deleteBudgetItem(DEFAULT_EVENT_ID, DEFAULT_BUDGET_ITEM_ID);

        verify(budget).removeItem(item);
        verify(eventRepository, times(1)).save(event);
    }

    @ParameterizedTest
    @Tag("delete-budget-item")
    @EnumSource(value = BudgetItemStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "PLANNED")
    void givenNonPlannedBudgetItem_whenDeleteBudgetItem_thenThrowAlreadyProcessedException(BudgetItemStatus status) {
        BudgetItem item = mock(BudgetItem.class);
        when(item.getId()).thenReturn(DEFAULT_BUDGET_ITEM_ID);
        when(item.getStatus()).thenReturn(status);
        Budget budget = mock(Budget.class);
        when(budget.getItems()).thenReturn(List.of(item));
        budget.addItem(item);
        mockEvent(budget);

        AlreadyProcessedException exception =  assertThrows(
                AlreadyProcessedException.class,
                () -> budgetService.deleteBudgetItem(DEFAULT_EVENT_ID, DEFAULT_BUDGET_ITEM_ID),
                "Solution is already processed"
        );
        assertEquals("Solution is already processed", exception.getMessage());
    }

    @Test
    @Tag("delete-budget-item")
    void givenNonExistentBudgetItem_whenDeleteBudgetItem_thenThrowEntityNotFoundException() {
        mockEvent(new Budget());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> budgetService.deleteBudgetItem(DEFAULT_EVENT_ID, DEFAULT_BUDGET_ITEM_ID)
        );
        assertEquals("Budget item not found.", exception.getMessage());
    }

    @Test
    @Tag("delete-budget-item")
    void givenNonExistentEvent_whenDeleteBudgetItem_thenThrowEntityNotFoundException() {
        when(eventService.find(anyLong())).thenThrow(new EntityNotFoundException("Event not found"));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> budgetService.deleteBudgetItem(DEFAULT_EVENT_ID, DEFAULT_BUDGET_ITEM_ID)
        );
        assertEquals("Event not found", exception.getMessage());
    }

    @Test
    @Tag("mark-reservation")
    void givenValidReservationWithMatchingServiceBudgetItem_whenMarkAsReserved_thenSetStatusAsProcessed() {
        Service service = new Service();
        service.setId(DEFAULT_BUDGET_ITEM_ID);
        service.setPrice(100.0);
        service.setDiscount(0.0);

        BudgetItem item = mock(BudgetItem.class);
        Reservation reservation = createReservationProcessedAt(null, item, service);

        budgetService.markAsReserved(reservation);

        verify(item).setStatus(BudgetItemStatus.PROCESSED);
        verify(item, times(1)).setProcessedAt(any(LocalDateTime.class));
        verify(budgetItemRepository, times(1)).save(item);
    }

    @Test
    @Tag("mark-reservation")
    void givenAlreadyProcessedBudgetItem_whenMarkAsReserved_thenThrowAlreadyProcessedException() {
        Service service = new Service();
        service.setId(DEFAULT_BUDGET_ITEM_ID);
        service.setPrice(100.0);
        service.setDiscount(0.0);

        BudgetItem item = mock(BudgetItem.class);
        Reservation reservation = createReservationProcessedAt(LocalDateTime.now(), item, service);

        AlreadyProcessedException exception = assertThrows(
                AlreadyProcessedException.class,
                () ->  budgetService.markAsReserved(reservation)
        );
        assertEquals("Service is already reserved", exception.getMessage());
    }

    @Test
    @Tag("mark-reservation")
    void givenMissingServiceBudgetItem_whenMarkAsReserved_thenThrowEntityNotFoundException() {
        Service service = new Service();
        service.setId(DEFAULT_BUDGET_ITEM_ID);
        service.setPrice(100.0);
        service.setDiscount(0.0);

        Event event = new Event();
        event.setId(DEFAULT_EVENT_ID);
        event.setBudget(new Budget());

        Reservation reservation = new Reservation();
        reservation.setService(service);
        reservation.setEvent(event);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () ->  budgetService.markAsReserved(reservation)
        );
        assertEquals("Matching budget item not found.", exception.getMessage());
    }

    @Test
    @Tag("create-budget-item")
    void givenValidItem_whenCreateBudgetItem_thenReturnCreatedBudgetItem() {
        Product product = new Product();
        product.setPrice(100.0);
        product.setDiscount(0.0);
        BudgetItemRequestDto request = createRequest(120.0);
        BudgetItem item = new BudgetItem();
        Budget budget = mock(Budget.class);
        when(budget.getItems()).thenReturn(Collections.emptyList());
        Event event = mockEvent(budget);

        when(solutionService.find(anyLong())).thenReturn(product);
        when(mapper.fromRequest(request, product)).thenReturn(item);
        when(mapper.toResponse(item)).thenReturn(new BudgetItemResponseDto());

        budgetService.createBudgetItem(DEFAULT_BUDGET_ITEM_ID, request);

        verify(eventRepository, times(1)).save(event);
        verify(budget).addItem(item);
        verify(mapper).toResponse(item);
    }

    @Test
    @Tag("create-budget-item")
    void givenNonExistentItem_whenCreateBudgetItem_thenThrowEntityNotFoundException() {
        BudgetItemRequestDto request = createRequest(120.0);
        when(solutionService.find(anyLong())).thenThrow(new EntityNotFoundException("Service not found"));
        mockEvent(new Budget());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> budgetService.createBudgetItem(DEFAULT_BUDGET_ITEM_ID, request)
        );
        assertEquals("Service not found", exception.getMessage());
    }

    @Test
    @Tag("create-budget-item")
    void givenInsufficientFunds_whenCreateBudgetItem_thenThrowInsufficientFundsException() {
        Product product = new Product();
        product.setPrice(120.0);
        product.setDiscount(0.0);
        BudgetItemRequestDto request = createRequest(0.0);
        Budget budget = new Budget();
        budget.setItems(Collections.emptyList());
        mockEvent(budget);
        when(solutionService.find(anyLong())).thenReturn(product);


        InsufficientFundsException exception = assertThrows(
                InsufficientFundsException.class,
                () ->  budgetService.createBudgetItem(DEFAULT_BUDGET_ITEM_ID, request)
        );
        assertEquals("You didn't plan to invest this much money.", exception.getMessage());
    }

    @Test
    @Tag("create-budget-item")
    void givenPlannedBudgetItem_whenCreateBudgetItem_thenUpdatePlannedAmount() {
        double plannedAmount = 200.0;
        Product product = new Product();
        product.setId(DEFAULT_BUDGET_ITEM_ID);
        product.setPrice(120.0);
        product.setDiscount(0.0);
        BudgetItem item = mock(BudgetItem.class);
        when(item.getSolution()).thenReturn(product);

        Budget budget = new Budget();
        budget.addItem(item);
        Event event = mockEvent(budget);

        BudgetItemRequestDto request = createRequest(plannedAmount);

        budgetService.createBudgetItem(DEFAULT_BUDGET_ITEM_ID, request);

        verify(item).setPlannedAmount(plannedAmount);
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    @Tag("create-budget-item")
    void givenProcessedBudgetItem_whenCreateBudgetItem_thenThrowAlreadyProcessedException() {
        Product product = new Product();
        product.setId(DEFAULT_BUDGET_ITEM_ID);
        product.setPrice(120.0);
        product.setDiscount(0.0);

        BudgetItem item = new BudgetItem();
        item.setPlannedAmount(200.0);
        item.setSolution(product);
        item.setProcessedAt(LocalDateTime.now());

        Budget budget = new Budget();
        budget.addItem(item);
        mockEvent(budget);

        BudgetItemRequestDto request = createRequest(200.0);

        AlreadyProcessedException exception = assertThrows(
                AlreadyProcessedException.class,
                () -> budgetService.createBudgetItem(DEFAULT_BUDGET_ITEM_ID, request)
        );
        assertEquals("Solution is already processed", exception.getMessage());
    }


    private Product mockProduct(double price, double discount) {
        Product product = mock(Product.class);
        when(product.getPrice()).thenReturn(price);
        when(product.getDiscount()).thenReturn(discount);
        when(productService.find(anyLong())).thenReturn(product);
        return product;
    }

    private BudgetItem mockProductBudgetItemProcessedAt(LocalDateTime processedAt) {
        Product product = new Product();
        product.setId(1L);
        product.setPrice(100.0);
        product.setDiscount(50.0);

        BudgetItem item = mock(BudgetItem.class);
        when(item.getProcessedAt()).thenReturn(processedAt);
        when(item.getId()).thenReturn(1L);
        when(item.getPlannedAmount()).thenReturn(100.0);
        when(item.getSolution()).thenReturn(product);
        return item;
    }

    private BudgetItemRequestDto createRequest(double plannedAmount) {
        return BudgetItemRequestDto.builder().itemId(1L).plannedAmount(plannedAmount).build();
    }

    private Event mockEvent(Budget budget) {
        Event event = mock(Event.class);
        when(eventService.find(anyLong())).thenReturn(event);
        when(event.getBudget()).thenReturn(budget);
        when(event.getOrganizer()).thenReturn(organizer);
        when(authService.getCurrentUser()).thenReturn(organizer);
        return event;
    }

    private Reservation createReservationProcessedAt(LocalDateTime processedAt, BudgetItem item, Service service) {
        when(item.getProcessedAt()).thenReturn(processedAt);
        when(item.getItemType()).thenReturn(SolutionType.SERVICE);
        when(item.getSolution()).thenReturn(service);

        Budget budget = new Budget();
        budget.addItem(item);

        Event event = new Event();
        event.setId(DEFAULT_EVENT_ID);
        event.setBudget(budget);

        Reservation reservation = new Reservation();
        reservation.setService(service);
        reservation.setEvent(event);

        return reservation;
    }
}
