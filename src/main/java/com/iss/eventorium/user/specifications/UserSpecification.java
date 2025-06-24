package com.iss.eventorium.user.specifications;

import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.user.models.Person;
import com.iss.eventorium.user.models.Role;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.models.UserBlock;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    private UserSpecification() {}

    public static Specification<User> filterByEventAttendance(Long eventId) {
        return (root, query, criteriaBuilder) -> {
            Join<User, Person> personJoin = root.join("person");
            Join<Person, Event> eventJoin = personJoin.join("attendingEvents");
            return criteriaBuilder.equal(eventJoin.get("id"), eventId);
        };
    }

    public static Specification<User> filterByRole(String roleName) {
        return Specification.where(hasRoleName(roleName));
    }

    public static Specification<User> filterByIdAndNotBlockedBy(Long userId, Long blockerId) {
        return (root, query, cb) -> {
            Predicate idMatch = cb.equal(root.get("id"), userId);

            if (blockerId == null) return idMatch;

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<UserBlock> userBlockRoot = subquery.from(UserBlock.class);

            subquery.select(userBlockRoot.get("blocked").get("id"))
                    .where(cb.equal(userBlockRoot.get("blocker").get("id"), blockerId));

            Predicate notBlocked = cb.not(root.get("id").in(subquery));
            return cb.and(idMatch, notBlocked);
        };
    }

    private static Specification<User> hasRoleName(String roleName) {
        return (root, query, criteriaBuilder) -> {
            Join<User, Role> roles = root.join("roles");
            return criteriaBuilder.equal(roles.get("name"), roleName);
        };
    }
}
