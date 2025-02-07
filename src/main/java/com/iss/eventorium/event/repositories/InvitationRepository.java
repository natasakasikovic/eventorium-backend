package com.iss.eventorium.event.repositories;

import com.iss.eventorium.event.models.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long>, JpaSpecificationExecutor<Invitation> {

    Optional<Invitation> findByHash(String hash);
}
