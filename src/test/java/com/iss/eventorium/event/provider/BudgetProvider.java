package com.iss.eventorium.event.provider;

import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.event.dtos.budget.BudgetItemRequestDto;
import com.iss.eventorium.solution.models.SolutionType;
import com.iss.eventorium.user.dtos.auth.LoginRequestDto;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class BudgetProvider {

    private static final CategoryResponseDto VALID_CATEGORY = new CategoryResponseDto(2L, "Catering", "Food and beverages arrangements");

    public static Stream<Arguments> provideBudgetItems() {
        return Stream.of(
                Arguments.of(new LoginRequestDto("organizer@gmail.com", "pera"), 3),
                Arguments.of(new LoginRequestDto("organizer2@gmail.com", "pera"), 2),
                Arguments.of(new LoginRequestDto("organizer3@gmail.com", "pera"), 1)
        );
    }

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
