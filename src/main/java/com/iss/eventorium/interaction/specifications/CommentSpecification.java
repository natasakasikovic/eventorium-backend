package com.iss.eventorium.interaction.specifications;

import com.iss.eventorium.interaction.models.Comment;
import com.iss.eventorium.interaction.models.CommentType;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.models.UserBlock;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

public class CommentSpecification {

    private CommentSpecification() {}

    public static Specification<Comment> filterBy(CommentType type, Long objectId, User user) {
        return Specification.where(hasObjectId(objectId))
                .and(hasCommentType(type))
                .and(filterOutBlockedContent(user))
                .and(hasStatus(Status.ACCEPTED));
    }

    public static Specification<Comment> filterPendingComments() {
        return Specification.where(hasStatus(Status.PENDING));
    }

    private static Specification<Comment> hasObjectId(Long objectId) {
        return (root, query, cb) -> objectId == null
                ? cb.conjunction()
                : cb.equal(root.get("objectId"), objectId);
    }

    private static Specification<Comment> hasStatus(Status status) {
        return (root, query, cb) -> status == null
                ? cb.conjunction()
                : cb.equal(root.get("status"), status);
    }

    private static Specification<Comment> hasCommentType(CommentType type) {
        return (root, query, cb) -> cb.equal(root.get("commentType"), type);
    }

    private static Specification<Comment> filterOutBlockedContent(User blocker) {
        return (root, query, cb) -> {
            if (blocker == null) return cb.conjunction();

            Long blockerId = blocker.getId();

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<UserBlock> userBlockRoot = subquery.from(UserBlock.class);

            subquery.select(userBlockRoot.get("blocked").get("id"))
                    .where(cb.equal(userBlockRoot.get("blocker").get("id"), blockerId));

            return cb.not(root.get("author").get("id").in(subquery));
        };
    }
}