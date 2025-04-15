package com.iss.eventorium.interaction.specifications;

import com.iss.eventorium.interaction.models.ChatRoom;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.models.UserBlock;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

public class ChatRoomSpecification {

    private ChatRoomSpecification() {}

    public static Specification<ChatRoom> filterBy(User user) {
        return Specification.where(nameLike(user.getId() + "_%"))
                .and(filterOutBlockedContent(user));
    }

    private static Specification<ChatRoom> nameLike(String senderId) {
        return (root, query, criteriaBuilder) -> {
            assert query != null;
            if (query.getOrderList().isEmpty()) {
                query.orderBy(criteriaBuilder.asc(root.get("lastMessage").get("timestamp")));
            }
            return criteriaBuilder.like(root.get("name"), senderId);
        };
    }

    private static Specification<ChatRoom> filterOutBlockedContent(User blocker) {
        return (root, query, cb) -> {
            if (blocker == null) return cb.conjunction();

            Long blockerId = blocker.getId();

            assert query != null;
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<UserBlock> userBlockRoot = subquery.from(UserBlock.class);

            subquery.select(userBlockRoot.get("blocked").get("id"))
                    .where(cb.equal(userBlockRoot.get("blocker").get("id"), blockerId));

            return cb.not(root.get("lastMessage").get("recipient").get("id").in(subquery));
        };
    }
}
