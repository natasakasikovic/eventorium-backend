package com.iss.eventorium.event.specifications;

import com.iss.eventorium.event.models.Budget;
import com.iss.eventorium.event.models.BudgetItem;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.models.UserBlock;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

public class BudgetSpecification {

    private BudgetSpecification() {}

    public static Specification<BudgetItem> filterForOrganizer(User organizer) {
        return filterBudgetItems(organizer.getId()).and(filterOutBlockedContent(organizer));
    }

    private static Specification<BudgetItem> filterBudgetItems(Long organizerId) {
        return (root, query, criteriaBuilder) -> {
            assert query != null;
            query.distinct(true);

            Root<Event> eventRoot = query.from(Event.class);
            Join<Event, Budget> budgetJoin = eventRoot.join("budget");
            budgetJoin.join("items");

            query.where(
                    criteriaBuilder.and(
                            criteriaBuilder.equal(eventRoot.get("organizer").get("id"), organizerId)
                    )
            );

            return query.getRestriction();
        };
    }

    private static Specification<BudgetItem> filterOutBlockedContent(User blocker) {
        return (root, query, cb) -> {
            if (blocker == null) return cb.conjunction();

            Long blockerId = blocker.getId();

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<UserBlock> userBlockRoot = subquery.from(UserBlock.class);

            subquery.select(userBlockRoot.get("blocked").get("id"))
                    .where(cb.equal(userBlockRoot.get("blocker").get("id"), blockerId));

            return cb.not(root.get("solution").get("provider").get("id").in(subquery));
        };
    }
}
