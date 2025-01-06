package com.iss.eventorium.solution.specifications;

import com.iss.eventorium.solution.dtos.products.ProductFilterDto;
import com.iss.eventorium.solution.models.Product;
import org.springframework.data.jpa.domain.Specification;

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
        return (root, query, cb) ->
                minPrice == null
                        ? cb.conjunction()
                        : cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Product> hasMaxPrice(Double maxPrice) {
        return (root, query, cb) ->
                maxPrice == null
                        ? cb.conjunction()
                        : cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<Product> hasAvailability(Boolean availability) {
        return (root, query, cb) ->
                availability == null
                ? cb.conjunction()
                        : cb.equal(root.get("isAvailable"), availability);
    }
}