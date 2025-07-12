package com.iss.eventorium.event.specifications;

import com.iss.eventorium.event.models.Budget;
import com.iss.eventorium.event.models.BudgetItem;
import com.iss.eventorium.event.models.BudgetItemStatus;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.solution.models.Solution;
import com.iss.eventorium.user.models.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class BudgetSpecification {

    private BudgetSpecification() {}

    public static Specification<BudgetItem> filterForOrganizer(User organizer) {
        return filterBudgetItems(organizer.getId());
    }

    public static Specification<BudgetItem> filterAllBudgetItems(User organizer) {
        return filterForOrganizer(organizer)
                .and(isNotDeleted())
                .and(isProcessed());
    }

    private static Specification<BudgetItem> isNotDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("solution").get("isDeleted"));
    }

    private static Specification<BudgetItem> filterBudgetItems(Long organizerId) {
        return (root, query, cb) -> {
            assert query != null;

            Root<Event> eventRoot = query.from(Event.class);

            Join<Event, Budget> budgetJoin = eventRoot.join("budget");
            Join<Budget, BudgetItem> itemsJoin = budgetJoin.join("items");

            Join<BudgetItem, Solution> solutionJoin = itemsJoin.join("solution");

            Predicate linkToRoot = cb.equal(root, itemsJoin);
            Predicate organizerMatches = cb.equal(eventRoot.get("organizer").get("id"), organizerId);
            Predicate isVisible = cb.isTrue(solutionJoin.get("isVisible"));

            return cb.and(linkToRoot, organizerMatches, isVisible);
        };
    }

    private static Specification<BudgetItem> isProcessed() {
        return (root, query, cb) -> cb.equal(root.get("status"), BudgetItemStatus.PROCESSED);
    }
}
