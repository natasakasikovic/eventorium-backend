package com.iss.eventorium.interaction.repositories;

import com.iss.eventorium.interaction.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
