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
        return Specification.where(filterBySenderId(user.getId() + "_%"))
                .and(filterOutBlockedContent(user));
    }

    public static Specification<ChatRoom> filterByName(String name) {
        return (root, query, cb) ->
                name == null || name.isEmpty()
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("name")), name);
    }

    private static Specification<ChatRoom> filterBySenderId(String senderId) {
        return (root, query, cb) -> {
            assert query != null;
            if (query.getOrderList().isEmpty()) {
                query.orderBy(cb.asc(root.get("lastMessage").get("timestamp")));
            }
            return cb.like(root.get("name"), senderId);
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
