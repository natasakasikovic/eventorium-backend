package com.iss.eventorium.interaction.repositories;

import com.iss.eventorium.interaction.models.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByName(String name);

    @Query("SELECT cm FROM chat_rooms cm WHERE cm.name LIKE :senderId ORDER BY cm.lastMessage.timestamp")
    List<ChatRoom> findChatRooms(String senderId);

    @Query("SELECT cm FROM chat_rooms cm WHERE cm.name LIKE :senderId ORDER BY cm.lastMessage.timestamp")
    Page<ChatRoom> findChatRooms(String senderId, Pageable pageable);
}
