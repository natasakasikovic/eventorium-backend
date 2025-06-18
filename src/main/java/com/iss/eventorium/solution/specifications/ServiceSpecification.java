package com.iss.eventorium.solution.specifications;

import com.iss.eventorium.solution.dtos.services.ServiceFilterDto;
import com.iss.eventorium.solution.models.Service;

import com.iss.eventorium.user.models.User;

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
        return Specification.where(SolutionSpecification.<Service>filterOutBlockedContent(user)
                .and(applyUserRoleFilter(user))
                .and(filterTopSolutions()));
    }

}
