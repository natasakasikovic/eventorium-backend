package com.iss.eventorium.event.provider;

import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.event.dtos.budget.BudgetItemRequestDto;
import com.iss.eventorium.event.models.BudgetItemStatus;
import com.iss.eventorium.solution.models.ReservationType;
import com.iss.eventorium.solution.models.SolutionType;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static com.iss.eventorium.util.TestUtil.*;

public class BudgetProvider {

    public static final CategoryResponseDto VALID_CATEGORY = new CategoryResponseDto(2L, "Catering", "Food and beverages arrangements");

    public static Stream<Long> provideArchiveProducts() {
        return Stream.of(DELETED_PRODUCT, INVISIBLE_PRODUCT);
    }

    public static Stream<Long> provideArchivedSolutions() {
        return Stream.of(DELETED_PRODUCT, INVISIBLE_PRODUCT, DELETED_SERVICE, INVISIBLE_SERVICE);
    }

    public static Stream<Arguments> provideInvalidBudgetItems() {
        return Stream.of(
                Arguments.of(
                        BudgetItemRequestDto.builder()
                                .itemId(1L)
                                .category(VALID_CATEGORY)
                                .plannedAmount(-50.0)
                                .itemType(SolutionType.PRODUCT)
                                .build(),
                        "Planned amount must be positive"
                ),
                Arguments.of(
                        BudgetItemRequestDto.builder()
                                .category(VALID_CATEGORY)
                                .plannedAmount(1000.0)
                                .itemType(SolutionType.PRODUCT)
                                .build(),
                        "Item is mandatory"
                ),
                Arguments.of(
                        BudgetItemRequestDto.builder()
                                .itemId(1L)
                                .plannedAmount(1000.0)
                                .category(VALID_CATEGORY)
                                .build(),
                        "Item type is mandatory"
                ),
                Arguments.of(
                        BudgetItemRequestDto.builder()
                                .itemId(1L)
                                .category(VALID_CATEGORY)
                                .itemType(SolutionType.PRODUCT)
                                .build(),
                        "Planned amount is mandatory"
                ),
                Arguments.of(
                        BudgetItemRequestDto.builder()
                                .itemId(1L)
                                .plannedAmount(1000.0)
                                .itemType(SolutionType.PRODUCT)
                                .build(),
                        "Category is mandatory"
                )
        );
    }

    public static Stream<Arguments> provideReservationTypeAndExpectedStatus() {
        return Stream.of(
                Arguments.of(ReservationType.AUTOMATIC, BudgetItemStatus.PROCESSED),
                Arguments.of(ReservationType.MANUAL, BudgetItemStatus.PENDING)
        );
    }

}
