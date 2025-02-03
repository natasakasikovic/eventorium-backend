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

    public static Specification<Product> filterBy(ProductFilterDto filter, User user) {
        return Specification.where(hasName(filter.getName()))
                .and(hasDescription(filter.getDescription()))
                .and(hasEventType(filter.getType()))
                .and(hasCategory(filter.getCategory()))
                .and(hasMinPrice(filter.getMinPrice()))
                .and(hasMaxPrice(filter.getMaxPrice()))
                .and(hasAvailability(filter.getAvailability())
                .and(filterOutBlockedContent(user)));
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
                            .and(filterOutBlockedContent(user));
    }

    public static Specification<Product> filterForProvider(User provider) {
        return Specification.where(hasProvider(provider.getId()));
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

    public static Specification<Product> filterOutBlockedContent(User blocker) {
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