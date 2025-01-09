package com.iss.eventorium.solution.specifications;

import com.iss.eventorium.solution.dtos.services.ServiceFilterDto;
import com.iss.eventorium.solution.models.Service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

public class ServiceSpecification {

    public static Specification<Service> filterBy(ServiceFilterDto filter, Long providerId) {
        return Specification
                .where(hasCategory(filter.getCategory()))
                .and(hasProvider(providerId))
                .and(hasEventType(filter.getType()))
                .and(hasMinPrice(filter.getMinPrice()))
                .and(hasMaxPrice(filter.getMaxPrice()))
                .and(hasAvailability(filter.getAvailability()));
    }

    public static Specification<Service> filterBy(ServiceFilterDto filter) {
        return Specification
                .where(hasName(filter.getName()))
                .and(hasDescription(filter.getDescription()))
                .and(hasCategory(filter.getCategory()))
                .and(hasEventType(filter.getType()))
                .and(hasMinPrice(filter.getMinPrice()))
                .and(hasMaxPrice(filter.getMaxPrice()))
                .and(hasAvailability(filter.getAvailability()));
    }

    public static Specification<Service> search(String keyword, Long providerId) {
        return Specification
                .where(hasName(keyword))
                .and(hasProvider(providerId));
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
                : cb.equal(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
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

    private static Expression<Double> calculateDiscountedPrice(Root<Service> root, CriteriaBuilder cb) {
        Expression<Double> discount = cb.prod(cb.diff(cb.literal(100.0), root.get("discount")), cb.literal(0.01));
        return cb.prod(root.get("price"), discount);
    }
}
