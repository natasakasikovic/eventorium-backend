package com.iss.eventorium.solution.specifications;

import com.iss.eventorium.interaction.models.Rating;
import com.iss.eventorium.solution.dtos.services.ServiceFilterDto;
import com.iss.eventorium.solution.models.Service;

import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.models.UserBlock;
import jakarta.persistence.criteria.*;

import org.springframework.data.jpa.domain.Specification;

import static com.iss.eventorium.solution.specifications.SolutionSpecification.*;

public class ServiceSpecification {

    private ServiceSpecification() {}

    public static Specification<Service> filterBy(ServiceFilterDto filter, User user) {
        return Specification
                .where(SolutionSpecification.<Service>hasName(filter.getName()))
                .and(hasDescription(filter.getDescription()))
                .and(hasCategory(filter.getCategory()))
                .and(hasEventType(filter.getType()))
                .and(hasMinPrice(filter.getMinPrice()))
                .and(hasMaxPrice(filter.getMaxPrice()))
                .and(SolutionSpecification.<Service>hasAvailability(filter.getAvailability())
                .and(filterOutBlockedContent(user))
                .and(applyUserRoleFilter(user)));
    }

    public static Specification<Service> filterForProvider(ServiceFilterDto filter, User user) {
        return filterBy(filter, user).and(hasProvider(user.getId()));
    }

    public static Specification<Service> filterByNameForProvider(String keyword, User user) {
        return Specification
                .where(SolutionSpecification.<Service>hasName(keyword))
                .and(hasProvider(user.getId()));
    }

    public static Specification<Service> filterByName(String keyword, User user) {
        return Specification.where(SolutionSpecification.<Service>hasName(keyword)
                .and(filterOutBlockedContent(user))
                .and(applyUserRoleFilter(user)));
    }

    public static Specification<Service> filterForProvider(User provider) {
        return Specification.where(hasProvider(provider.getId()));
    }

    public static Specification<Service> filter(User user) {
        return Specification.where(SolutionSpecification.<Service>filterOutBlockedContent(user)
                            .and(applyUserRoleFilter(user)));
    }

    public static Specification<Service> filterById(Long id, User user) {
        return Specification.where(SolutionSpecification.<Service>hasId(id)
                .and(filterOutBlockedContent(user)))
                .and(applyUserRoleFilter(user));
    }

    public static Specification<Service> filterTopServices(User user) {
        return Specification.where(filterOutBlockedContent(user))
                .and(applyUserRoleFilter(user))
                .and((root, query, cb) -> {

                    Join<Service, Rating> ratingJoin = root.join("ratings");
                    query.groupBy(root);
                    Expression<Double> avgRating = cb.avg(ratingJoin.get("rating"));
                    query.orderBy(cb.desc(avgRating));

                    return cb.conjunction();
                });
    }

    private static Specification<Service> hasId(Long id){
        return (root, query, cb) -> cb.equal(root.get("id"), id);
    }

    private static Specification<Service> hasProvider(Long providerId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("provider").get("id"), providerId);
    }

    private static Specification<Service> hasName(String name) {
        return (root, query, cb) ->
                name == null || name.isEmpty()
                 ? cb.conjunction()
                 : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private static Specification<Service> hasDescription(String description) {
        return (root, query, cb) ->
                description == null || description.isEmpty()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
    }

    private static Specification<Service> hasAvailability(Boolean available) {
        return (root, query, cb) ->
                available == null || !available
                        ? cb.conjunction()
                        : cb.equal(root.get("isAvailable"), true);
    }

    private static Specification<Service> hasEventType(String type) {
        return (root, query, cb) ->
                type == null ? cb.conjunction()
                        : cb.equal(root.join("eventTypes").get("name"), type);
    }

    private static Specification<Service> hasCategory(String category) {
        return (root, query, cb) ->
                category == null || category.isEmpty()
                        ? cb.conjunction()
                        : cb.equal(cb.lower(root.get("category").get("name")), category.toLowerCase());
    }

    private static Specification<Service> hasMinPrice(Double minPrice) {
        return (root, query, cb) -> {
            if (minPrice == null) return cb.conjunction();
            Expression<Double> discountedPrice = calculateDiscountedPrice(root, cb);
            return cb.greaterThanOrEqualTo(discountedPrice, minPrice);
        };
    }

    private static Specification<Service> hasMaxPrice(Double maxPrice) {
        return (root, query, cb) -> {
            if (maxPrice == null) return cb.conjunction();
            Expression<Double> discountedPrice = calculateDiscountedPrice(root, cb);
            return cb.lessThanOrEqualTo(discountedPrice, maxPrice);
        };
    }

    private static Specification<Service> applyUserRoleFilter(User user) {
        return (root, query, cb) -> {
            // Unauthenticated users and organizers can only see visible and accepted services
            if (user == null || user.getRoles().stream().anyMatch(role -> "EVENT_ORGANIZER".equals(role.getName()))) {
                return cb.and(
                        cb.isTrue(root.get("isVisible")),
                        cb.equal(root.get("status"), "ACCEPTED")
                );
            }

            // Allow admin to see every service.
            if (user.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName()))) {
                return cb.conjunction();
            }

            // Provider can see his hidden and pending services.
            return cb.or(
                    cb.and(cb.equal(root.get("status"), "ACCEPTED"), cb.isTrue(root.get("isVisible"))),
                    cb.equal(root.get("provider").get("id"), user.getId())
            );
        };
    }

    private static Specification<Service> filterOutBlockedContent(User blocker) {
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

    private static Expression<Double> calculateDiscountedPrice(Root<Service> root, CriteriaBuilder cb) {
        Expression<Double> discount = cb.prod(cb.diff(cb.literal(100.0), root.get("discount")), cb.literal(0.01));
        return cb.prod(root.get("price"), discount);
    }
}
