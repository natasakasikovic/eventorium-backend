package com.iss.eventorium.shared.models;

import com.iss.eventorium.interaction.models.Comment;
import com.iss.eventorium.user.models.User;

import java.util.List;

public interface Commentable {
    List<Comment> getComments();
    String getDisplayName();
    Long getId();
    User getCreator();
}
