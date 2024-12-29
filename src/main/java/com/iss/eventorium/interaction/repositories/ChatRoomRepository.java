package com.iss.eventorium.interaction.repositories;


import com.iss.eventorium.interaction.models.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findBySender_IdAndRecipient_Id(Long sender_id, Long recipient_id);
}
