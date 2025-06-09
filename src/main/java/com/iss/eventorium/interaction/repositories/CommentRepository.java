package com.iss.eventorium.interaction.repositories;

import com.iss.eventorium.interaction.models.Comment;
import com.iss.eventorium.shared.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
    List<Comment> findByStatusOrderByCreationDateDesc(Status status);
}