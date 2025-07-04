package com.iss.eventorium.user.repositories;

import com.iss.eventorium.user.models.UserBlock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {
}