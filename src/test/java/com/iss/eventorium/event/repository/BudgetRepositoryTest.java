package com.iss.eventorium.event.repository;

import com.iss.eventorium.event.models.BudgetItem;
import com.iss.eventorium.event.models.BudgetItemStatus;
import com.iss.eventorium.event.repositories.BudgetItemRepository;
import com.iss.eventorium.event.specifications.BudgetSpecification;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
@Sql(
        scripts = "/sql/budget-repository-test-data.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
class BudgetRepositoryTest {

    @Autowired
    private BudgetItemRepository budgetItemRepository;

    @Autowired
    private UserRepository userRepository;

    private User organizer;

    @BeforeEach
    void setUp() {
        organizer = userRepository.findByEmail("organizer@gmail.com").orElseThrow();
    }

    @ParameterizedTest
    @CsvSource({
            "organizer@gmail.com, 3",
            "organizer2@gmail.com, 4",
    })
    void givenValidItem_whenFilterAllBudgetItems_thenItemReturned(String email, int expectedSize) {
        organizer = userRepository.findByEmail(email).orElseThrow();
        List<BudgetItem> items = budgetItemRepository.findAll(
                BudgetSpecification.filterAllBudgetItems(organizer)
        );

        assertEquals(expectedSize, items.size());
        assertEquals(BudgetItemStatus.PROCESSED, items.get(0).getStatus());
    }

    @Test
    void givenDeletedSolution_whenFilterAllBudgetItems_thenItemExcluded() {
        BudgetItem item = budgetItemRepository.findAll().get(0);
        item.getSolution().setIsDeleted(true);
        budgetItemRepository.save(item);

        List<BudgetItem> filtered = budgetItemRepository.findAll(
                BudgetSpecification.filterAllBudgetItems(organizer)
        );

        assertEquals(2, filtered.size());
    }

    @Test
    void givenUnprocessedItem_whenFilterAllBudgetItems_thenItemExcluded() {
        BudgetItem item = budgetItemRepository.findAll().get(0);
        item.setStatus(BudgetItemStatus.PLANNED);
        budgetItemRepository.save(item);

        List<BudgetItem> filtered = budgetItemRepository.findAll(
                BudgetSpecification.filterAllBudgetItems(organizer)
        );

        assertEquals(2, filtered.size());
    }


    @Test
    void givenInvisibleSolution_whenFilterAllBudgetItems_thenItemExcluded() {
        BudgetItem item = budgetItemRepository.findAll().get(0);
        item.getSolution().setIsVisible(false);
        budgetItemRepository.save(item);

        List<BudgetItem> filtered = budgetItemRepository.findAll(
                BudgetSpecification.filterAllBudgetItems(organizer)
        );

        assertEquals(2, filtered.size());
    }
}
