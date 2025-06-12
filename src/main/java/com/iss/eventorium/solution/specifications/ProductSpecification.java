package com.iss.eventorium.solution.specifications;

import com.iss.eventorium.solution.dtos.products.ProductFilterDto;
import com.iss.eventorium.solution.models.Product;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.models.UserBlock;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Expression;

public class ProductSpecification {

    private ProductSpecification() {}

    public static Specification<Product> filterBy(ProductFilterDto filter, User user) {
        return Specification.where(hasName(filter.getName()))
                .and(hasDescription(filter.getDescription()))
                .and(hasEventType(filter.getType()))
                .and(hasCategory(filter.getCategory()))
                .and(hasMinPrice(filter.getMinPrice()))
                .and(hasMaxPrice(filter.getMaxPrice()))
                .and(hasAvailability(filter.getAvailability())
                .and(filterOutBlockedContent(user))
                .and(applyUserRoleFilter(user)));
    }

    public static Specification<Product> filterForProvider(ProductFilterDto filter, User user) {
        return filterBy(filter, user).and(hasProvider(user.getId()));
    }

    public static Specification<Product> filterByNameForProvider(String keyword, User user) {
        return Specification.where(hasName(keyword))
                            .and(hasProvider(user.getId()));
    }

    public static Specification<Product> filterByName(String keyword, User user) {
        return Specification.where(hasName(keyword))
                            .and(filterOutBlockedContent(user)
                            .and(applyUserRoleFilter(user)));
    }

    public static Specification<Product> filterForProvider(User provider) {
        return Specification.where(hasProvider(provider.getId()));
    }

    public static Specification<Product> filter(User user) {
        return Specification.where(filterOutBlockedContent(user)
                .and(applyUserRoleFilter(user)));
    }

    public static Specification<Product> filterById(Long id, User user) {
        return Specification.where(hasId(id)
                .and(filterOutBlockedContent(user)))
                .and(applyUserRoleFilter(user));
    }

    private static Specification<Product> hasId(Long id){
        return (root, query, cb) -> cb.equal(root.get("id"), id);
    }

    private static Specification<Product> hasProvider(Long providerId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("provider").get("id"), providerId);
    }

    private static Specification<Product> hasName(String name) {
        return (root, query, cb) ->
                name == null ? cb.conjunction()
                        : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private static Specification<Product> hasDescription(String description) {
        return (root, query, cb) ->
                description == null ? cb.conjunction()
                        : cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
    }

    private static Specification<Product> hasEventType(String type) {
        return (root, query, cb) ->
                type == null ? cb.conjunction()
                        : cb.equal(root.join("eventTypes").get("name"), type);
    }

    private static Specification<Product> hasCategory(String category) {
        return ((root, query, cb) ->
                category == null ? cb.conjunction()
                        : cb.equal(root.get("category").get("name"), category));
    }

    private static Specification<Product> hasMinPrice(Double minPrice) {
        return (root, query, cb) -> {
            if (minPrice == null) return cb.conjunction();

            Expression<Double> discountedPrice = calculateDiscountedPrice(root, cb);

            return cb.greaterThanOrEqualTo(discountedPrice, minPrice);
        };
    }

    private static Specification<Product> hasMaxPrice(Double maxPrice) {
        return (root, query, cb) -> {
            if (maxPrice == null) return cb.conjunction();

            Expression<Double> discountedPrice = calculateDiscountedPrice(root, cb);

            return cb.lessThanOrEqualTo(discountedPrice, maxPrice);
        };
    }

    private static Specification<Product> hasAvailability(Boolean availability) {
        return (root, query, cb) ->
                availability == null
                        ? cb.conjunction()
                        : cb.equal(root.get("isAvailable"), availability);
    }

    private static Specification<Product> applyUserRoleFilter(User user) {
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

    private static Specification<Product> filterOutBlockedContent(User blocker) {
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

    private static Expression<Double> calculateDiscountedPrice(Root<Product> root, CriteriaBuilder cb) {
        Expression<Double> discount = cb.prod(cb.diff(cb.literal(100.0), root.get("discount")), cb.literal(0.01));
        return cb.prod(root.get("price"), discount);
    }

}