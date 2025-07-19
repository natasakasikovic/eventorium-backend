package com.iss.eventorium.event.repository;

import com.iss.eventorium.event.models.BudgetItem;
import com.iss.eventorium.event.models.BudgetItemStatus;
import com.iss.eventorium.event.repositories.BudgetItemRepository;
import com.iss.eventorium.event.specifications.BudgetSpecification;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class BudgetRepositoryTest {

    @Autowired
    private BudgetItemRepository budgetItemRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User organizer;

    @BeforeEach
    void setUp() {
        organizer = entityManager.find(User.class, 1L);
    }

    @ParameterizedTest
    @CsvSource({
            "1,3",
            "2,4",
    })
    @DisplayName("Should return only processed budget items for given organizer")
    void givenValidItem_whenFilterAllBudgetItems_thenItemReturned(Long id, int expectedSize) {
        organizer = entityManager.find(User.class, id);
        List<BudgetItem> items = budgetItemRepository.findAll(
                BudgetSpecification.filterAllBudgetItems(organizer)
        );

        assertEquals(expectedSize, items.size());
        assertEquals(BudgetItemStatus.PROCESSED, items.get(0).getStatus());
    }

    @Test
    @DisplayName("Should exclude budget item when related solution is marked as deleted")
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
    @DisplayName("Should exclude budget item when status is not processed")
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
    @DisplayName("Should exclude budget item when related solution is not visible")
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
