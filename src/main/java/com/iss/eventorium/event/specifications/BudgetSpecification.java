package com.iss.eventorium.event.specifications;

import com.iss.eventorium.event.models.Budget;
import com.iss.eventorium.event.models.BudgetItem;
import com.iss.eventorium.event.models.Event;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class BudgetSpecification {

    public static Specification<BudgetItem> filterForOrganizer(Long organizerId) {
        return (root, query, criteriaBuilder) -> {
            assert query != null;
            query.distinct(true);

            Root<Event> eventRoot = query.from(Event.class);
            Join<Event, Budget> budgetJoin = eventRoot.join("budget");
            Join<Budget, BudgetItem> budgetItemJoin = budgetJoin.join("items");

            query.where(
                    criteriaBuilder.and(
                            criteriaBuilder.equal(eventRoot.get("organizer").get("id"), organizerId),
                            criteriaBuilder.isNotNull(budgetItemJoin.get("purchased"))
                    )
            );

            return query.getRestriction();
        };
    }
}
