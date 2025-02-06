package com.iss.eventorium.shared.models;

import com.iss.eventorium.interaction.models.Comment;

import java.util.List;

public interface Commentable {
    List<Comment> getComments();
    String getDisplayName();
    Long getId();
}
