package com.iss.eventorium.event.repositories;

import com.iss.eventorium.event.models.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    Optional<Invitation> findByHash(String hash);
}
