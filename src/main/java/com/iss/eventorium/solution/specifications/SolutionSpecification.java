package com.iss.eventorium.solution.specifications;

import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.solution.models.Service;
import com.iss.eventorium.solution.models.Solution;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.models.UserBlock;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class SolutionSpecification {

    private SolutionSpecification() {}

    public static Specification<Solution> filterSuggestions(User user, Category category, double maxPrice,LocalDate eventDate) {
        return Specification.where(hasCategory(category.getName()))
                .and(isAvailability())
                .and(isVisible())
                .and(hasMaxDiscountedPrice(maxPrice))
                .and(serviceBeforeReservationDeadline(eventDate))
                .and(filterOutBlockedContent(user));
    }

    private static Specification<Solution> isVisible() {
        return (root, query, cb) -> cb.isTrue(root.get("isVisible"));
    }

    private static Specification<Solution> hasCategory(String category) {
        return (root, query, cb) ->
                category == null || category.isEmpty()
                        ? cb.conjunction()
                        : cb.equal(cb.lower(root.get("category").get("name")), category.toLowerCase());
    }

    private static Specification<Solution> isAvailability() {
        return (root, query, cb) -> cb.equal(root.get("isAvailable"), true);
    }


    public static Specification<Solution> serviceBeforeReservationDeadline(LocalDate eventDate) {
        return (root, query, cb) -> {
            if (eventDate == null) return cb.conjunction();

            LocalDate today = LocalDate.now();
            long daysUntilEvent = ChronoUnit.DAYS.between(today, eventDate);
            int daysUntilEventInt = (int) daysUntilEvent;

            if (daysUntilEventInt < 0) {
                return cb.disjunction();
            }

            Predicate isNotService = cb.notEqual(root.type(), Service.class);

            Root<Service> serviceRoot = cb.treat(root, Service.class);
            Path<Integer> reservationDeadline = serviceRoot.get("reservationDeadline");

            Predicate isWithinDeadline = cb.lessThanOrEqualTo(reservationDeadline, daysUntilEventInt);

            return cb.or(isNotService, isWithinDeadline);
        };
    }

    private static Specification<Solution> hasMaxDiscountedPrice(Double maxPrice) {
        return (root, query, cb) -> {
            if (maxPrice == null) {
                return cb.conjunction();
            }

            Expression<Double> discountedPrice = calculateDiscountedPrice(root, cb);
            return cb.lessThanOrEqualTo(discountedPrice, maxPrice);
        };
    }

    private static Specification<Solution> filterOutBlockedContent(User blocker) {
        return (root, query, cb) -> {
            if (blocker == null) return cb.conjunction();
            Long blockerId = blocker.getId();

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<UserBlock> userBlockRoot = subquery.from(UserBlock.class);

            subquery.select(userBlockRoot.get("blocked").get("id"))
                    .where(cb.equal(userBlockRoot.get("blocker").get("id"), blockerId));

            return cb.not(root.get("provider").get("id").in(subquery));
        };
    }

    public static Expression<Double> calculateDiscountedPrice(Root<? extends Solution> root, CriteriaBuilder cb) {
        Expression<Double> discount = cb.prod(cb.diff(cb.literal(100.0), root.get("discount")), cb.literal(0.01));
        return cb.prod(root.get("price"), discount);
    }

}
