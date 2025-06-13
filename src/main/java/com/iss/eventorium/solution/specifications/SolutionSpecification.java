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
                .and(hasAvailability(true))
                .and(isVisible())
                .and(hasMaxPrice(maxPrice))
                .and(serviceBeforeReservationDeadline(eventDate))
                .and(filterOutBlockedContent(user));
    }

    private static Specification<Solution> isVisible() {
        return (root, query, cb) -> cb.isTrue(root.get("isVisible"));
    }

    public static<T extends Solution> Specification<T> hasCategory(String category) {
        return (root, query, cb) ->
                category == null || category.isEmpty()
                        ? cb.conjunction()
                        : cb.equal(cb.lower(root.get("category").get("name")), category.toLowerCase());
    }

    public static<T extends Solution> Specification<T> hasProvider(Long providerId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("provider").get("id"), providerId);
    }


    public static<T extends Solution> Specification<T> hasName(String name) {
        return (root, query, cb) ->
                name == null ? cb.conjunction()
                        : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static<T extends Solution> Specification<T> hasEventType(String type) {
        return (root, query, cb) ->
                type == null ? cb.conjunction()
                        : cb.equal(root.join("eventTypes").get("name"), type);
    }

    public static<T extends Solution> Specification<T> hasDescription(String description) {
        return (root, query, cb) ->
                description == null ? cb.conjunction()
                        : cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
    }

    public static<T extends Solution> Specification<T> hasMaxPrice(Double maxPrice) {
        return (root, query, cb) -> {
            if (maxPrice == null) {
                return cb.conjunction();
            }

            Expression<Double> discountedPrice = calculateDiscountedPrice(root, cb);
            return cb.lessThanOrEqualTo(discountedPrice, maxPrice);
        };
    }

    public static<T extends Solution> Specification<T> hasAvailability(Boolean availability) {
        return (root, query, cb) ->
                availability == null
                        ? cb.conjunction()
                        : cb.equal(root.get("isAvailable"), availability);
    }

    public static<T extends Solution> Specification<T> hasMinPrice(Double minPrice) {
        return (root, query, cb) -> {
            if (minPrice == null) return cb.conjunction();

            Expression<Double> discountedPrice = SolutionSpecification.calculateDiscountedPrice(root, cb);

            return cb.greaterThanOrEqualTo(discountedPrice, minPrice);
        };
    }

    public static Expression<Double> calculateDiscountedPrice(Root<? extends Solution> root, CriteriaBuilder cb) {
        Expression<Double> discount = cb.prod(cb.diff(cb.literal(100.0), root.get("discount")), cb.literal(0.01));
        return cb.prod(root.get("price"), discount);
    }

    public static<T extends Solution> Specification<T> hasId(Long id){
        return (root, query, cb) -> cb.equal(root.get("id"), id);
    }

    public static<T extends Solution> Specification<T> filterOutBlockedContent(User blocker) {
        return (root, query, cb) -> {
            if (blocker == null) return cb.conjunction();
            Long blockerId = blocker.getId();

            assert query != null;
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<UserBlock> userBlockRoot = subquery.from(UserBlock.class);

            subquery.select(userBlockRoot.get("blocked").get("id"))
                    .where(cb.equal(userBlockRoot.get("blocker").get("id"), blockerId));

            return cb.not(root.get("provider").get("id").in(subquery));
        };
    }

    public static<T extends Solution> Specification<T> applyUserRoleFilter(User user) {
        return (root, query, cb) -> {
            // Unauthenticated users and organizers can only see visible and accepted products
            if (user == null || user.getRoles().stream().anyMatch(role -> "EVENT_ORGANIZER".equals(role.getName()))) {
                return cb.and(
                        cb.isTrue(root.get("isVisible")),
                        cb.equal(root.get("status"), "ACCEPTED")
                );
            }

            // Allow admin to see every product.
            if (user.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName()))) {
                return cb.conjunction();
            }

            // Provider can see his hidden and pending products.
            return cb.or(
                    cb.and(cb.equal(root.get("status"), "ACCEPTED"), cb.isTrue(root.get("isVisible"))),
                    cb.equal(root.get("provider").get("id"), user.getId())
            );
        };
    }

    private static Specification<Solution> serviceBeforeReservationDeadline(LocalDate eventDate) {
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

}
