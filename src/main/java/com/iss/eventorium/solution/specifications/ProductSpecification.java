package com.iss.eventorium.solution.specifications;

import com.iss.eventorium.solution.dtos.products.ProductFilterDto;
import com.iss.eventorium.solution.models.Product;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Expression;

public class ProductSpecification {

    public static Specification<Product> filterBy(ProductFilterDto filter) {
        return Specification.where(hasName(filter.getName()))
                .and(hasDescription(filter.getDescription()))
                .and(hasEventType(filter.getType()))
                .and(hasCategory(filter.getCategory()))
                .and(hasMinPrice(filter.getMinPrice()))
                .and(hasMaxPrice(filter.getMaxPrice()))
                .and(hasAvailability(filter.getAvailability()));
    }

    public static Specification<Product> filterBy(ProductFilterDto filter, Long providerId) {
        return filterBy(filter).and(hasProvider(providerId));
    }

    public static Specification<Product> search(String keyword, Long providerId) {
        return Specification.where(hasName(keyword)).and(hasProvider(providerId));
    }

    private static Specification<Product> hasProvider(Long providerId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("provider").get("id"), providerId);
    }

    public static Specification<Product> hasName(String name) {
        return (root, query, cb) ->
                name == null ? cb.conjunction()
                        : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Product> hasDescription(String description) {
        return (root, query, cb) ->
                description == null ? cb.conjunction()
                        : cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
    }

    public static Specification<Product> hasEventType(String type) {
        return (root, query, cb) ->
                type == null ? cb.conjunction()
                        : cb.equal(root.join("eventTypes").get("name"), type);
    }

    public static Specification<Product> hasCategory(String category) {
        return ((root, query, cb) ->
                category == null ? cb.conjunction()
                        : cb.equal(root.get("category").get("name"), category));
    }

    public static Specification<Product> hasMinPrice(Double minPrice) {
        return (root, query, cb) -> {
            if (minPrice == null) return cb.conjunction();

            Expression<Double> discountedPrice = calculateDiscountedPrice(root, cb);

            return cb.greaterThanOrEqualTo(discountedPrice, minPrice);
        };
    }

    public static Specification<Product> hasMaxPrice(Double maxPrice) {
        return (root, query, cb) -> {
            if (maxPrice == null) return cb.conjunction();

            Expression<Double> discountedPrice = calculateDiscountedPrice(root, cb);

            return cb.lessThanOrEqualTo(discountedPrice, maxPrice);
        };
    }

    public static Specification<Product> hasAvailability(Boolean availability) {
        return (root, query, cb) ->
                availability == null
                        ? cb.conjunction()
                        : cb.equal(root.get("isAvailable"), availability);
    }

    private static Expression<Double> calculateDiscountedPrice(Root<Product> root, CriteriaBuilder cb) {
        Expression<Double> discount = cb.prod(cb.diff(cb.literal(100.0), root.get("discount")), cb.literal(0.01));
        return cb.prod(root.get("price"), discount);
    }

}