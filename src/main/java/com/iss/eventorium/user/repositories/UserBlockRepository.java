package com.iss.eventorium.user.repositories;

import com.iss.eventorium.user.models.UserBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {

    @Query("SELECT ub.blocked.id FROM UserBlock ub WHERE ub.blocker.id = :blockerId")
    List<Long> findBlockedUsersByBlockerId(@Param("blockerId") Long blockerId);
}