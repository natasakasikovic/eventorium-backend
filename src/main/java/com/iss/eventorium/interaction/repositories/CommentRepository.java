package com.iss.eventorium.interaction.repositories;

import com.iss.eventorium.interaction.models.Comment;
import com.iss.eventorium.shared.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByStatus(Status status);
}
