package com.iss.eventorium.interaction.repositories;

import com.iss.eventorium.interaction.models.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, JpaSpecificationExecutor<ChatRoom> {
}
