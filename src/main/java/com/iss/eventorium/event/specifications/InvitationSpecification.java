package com.iss.eventorium.event.specifications;

import com.iss.eventorium.event.models.Invitation;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.models.UserBlock;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

public class InvitationSpecification {

    private InvitationSpecification() {}

    public static Specification<Invitation> filterForInvitedUser(User user) {
        return Specification.where(hasEmail(user.getEmail())).and(filterOutBlockedEvents(user));
    }

    private static Specification<Invitation> hasEmail(String email) {
        return (root, query, cb) -> cb.equal(root.get("email"), email);
    }

    private static Specification<Invitation> filterOutBlockedEvents(User blocker) {
        return (root, query, cb) -> {
            if (blocker == null) return cb.conjunction();

            Long blockerId = blocker.getId();

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<UserBlock> userBlockRoot = subquery.from(UserBlock.class);

            subquery.select(userBlockRoot.get("blocked").get("id"))
                    .where(cb.equal(userBlockRoot.get("blocker").get("id"), blockerId));

            return cb.not(root.get("event").get("organizer").get("id").in(subquery));
        };
    }
}
