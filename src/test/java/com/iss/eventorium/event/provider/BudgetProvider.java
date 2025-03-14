package com.iss.eventorium.event.provider;

import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.event.dtos.budget.BudgetItemRequestDto;
import com.iss.eventorium.solution.models.SolutionType;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class BudgetProvider {

    private static final CategoryResponseDto VALID_CATEGORY = new CategoryResponseDto(2L, "Catering", "Food and beverages arrangements");

    public static Stream<Arguments> provideInvalidBudgetItems() {
        return Stream.of(
                Arguments.of(BudgetItemRequestDto.builder().build()),
                Arguments.of(
                    BudgetItemRequestDto.builder()
                            .itemId(1L)
                            .category(VALID_CATEGORY)
                            .plannedAmount(-50.0)
                            .itemType(SolutionType.PRODUCT)
                            .build()
                ),
                Arguments.of(
                        BudgetItemRequestDto.builder()
                                .category(VALID_CATEGORY)
                                .plannedAmount(1000.0)
                                .itemType(SolutionType.PRODUCT)
                                .build()
                ),
                Arguments.of(
                        BudgetItemRequestDto.builder()
                                .itemId(1L)
                                .category(VALID_CATEGORY)
                                .itemType(SolutionType.PRODUCT)
                                .build()
                ),
                Arguments.of(
                        BudgetItemRequestDto.builder()
                                .itemId(1L)
                                .plannedAmount(1000.0)
                                .itemType(SolutionType.PRODUCT)
                                .build()
                )
        );
    }

}
