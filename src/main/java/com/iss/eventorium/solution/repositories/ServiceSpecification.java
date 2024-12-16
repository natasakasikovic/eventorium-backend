package com.iss.eventorium.solution.repositories;

import com.iss.eventorium.solution.dtos.services.ServiceFilterDto;
import com.iss.eventorium.solution.models.Service;
import org.springframework.data.jpa.domain.Specification;

public class ServiceSpecification {

    public static Specification<Service> filterBy(ServiceFilterDto filter, Long providerId) {
        return Specification
                .where(hasCategory(filter.getCategory()))
                .and(hasProvider(providerId))
                .and(hasEventType(filter.getEventType()))
                .and(hasPriceBetween(filter.getMinPrice(), filter.getMaxPrice()))
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

    private static Specification<Service> hasAvailability(Boolean available) {
        return (root, query, cb) ->
                available == null
                        ? cb.conjunction()
                        : cb.equal(root.get("isAvailable"), available);
    }

    private static Specification<Service> hasEventType(String eventType) {
        return (root, query, cb) ->
                eventType == null || eventType.isEmpty()
                        ? cb.conjunction()
                        : cb.equal(cb.lower(root.get("eventType").get("name")), eventType.toLowerCase());
    }

    private static Specification<Service> hasCategory(String category) {
        return (root, query, cb) ->
                category == null || category.isEmpty()
                        ? cb.conjunction()
                        : cb.equal(cb.lower(root.get("category").get("name")), category.toLowerCase());
    }

    public static Specification<Service> hasMinPrice(Double minPrice) {
        return (root, query, cb) ->
                minPrice == null
                        ? cb.conjunction()
                        : cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Service> hasMaxPrice(Double maxPrice) {
        return (root, query, cb) ->
                maxPrice == null
                        ? cb.conjunction()
                        : cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<Service> hasPriceBetween(Double minPrice, Double maxPrice) {
        return Specification.where(hasMinPrice(minPrice)).and(hasMaxPrice(maxPrice));
    }

}
