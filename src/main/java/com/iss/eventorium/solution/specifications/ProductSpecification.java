package com.iss.eventorium.solution.specifications;

import com.iss.eventorium.solution.dtos.products.ProductFilterDto;
import com.iss.eventorium.solution.models.Product;
import com.iss.eventorium.user.models.User;
import org.springframework.data.jpa.domain.Specification;

import static com.iss.eventorium.solution.specifications.SolutionSpecification.*;

public class ProductSpecification {

    private ProductSpecification() {}

    public static Specification<Product> filterBy(ProductFilterDto filter, User user) {
        return Specification.where(SolutionSpecification.<Product>hasName(filter.getName()))
                .and(hasDescription(filter.getDescription()))
                .and(hasEventType(filter.getType()))
                .and(hasCategory(filter.getCategory()))
                .and(hasMinPrice(filter.getMinPrice()))
                .and(hasMaxPrice(filter.getMaxPrice()))
                .and(SolutionSpecification.<Product>hasAvailability(filter.getAvailability())
                .and(filterOutBlockedContent(user))
                .and(applyUserRoleFilter(user)));
    }

    public static Specification<Product> filterForProvider(ProductFilterDto filter, User user) {
        return filterBy(filter, user).and(SolutionSpecification.hasProvider(user.getId()));
    }

    public static Specification<Product> filterByNameForProvider(String keyword, User user) {
        return Specification.where(SolutionSpecification.<Product>hasName(keyword))
                            .and(hasProvider(user.getId()));
    }

    public static Specification<Product> filterByName(String keyword, User user) {
        return Specification.where(SolutionSpecification.<Product>hasName(keyword))
                            .and(SolutionSpecification.<Product>filterOutBlockedContent(user)
                            .and(applyUserRoleFilter(user)));
    }

    public static Specification<Product> filterForProvider(User provider) {
        return Specification.where(hasProvider(provider.getId()));
    }

    public static Specification<Product> filter(User user) {
        return Specification.where(SolutionSpecification.<Product>filterOutBlockedContent(user)
                .and(applyUserRoleFilter(user)));
    }

    public static Specification<Product> filterById(Long id, User user, boolean includeDeleted) {
        Specification<Product> spec = SolutionSpecification.<Product>hasId(id)
                .and(filterOutBlockedContent(user))
                .and(applyUserRoleFilter(user));

        if (!includeDeleted) {
            spec = spec.and((root, query, cb) -> cb.isFalse(root.get("isDeleted")));
        }

        return spec;
    }

    public static Specification<Product> filterTopProducts(User user) {
        return Specification.where(SolutionSpecification.<Product>filterOutBlockedContent(user))
                .and(SolutionSpecification.applyUserRoleFilter(user))
                .and(SolutionSpecification.filterTopSolutions());
    }
}