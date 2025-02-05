package com.iss.eventorium.shared.models;

import com.iss.eventorium.interaction.models.Comment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
public abstract class CommentableEntity implements Commentable {

    @ManyToMany
    private List<Comment> comments = new ArrayList<>();

    @Override
    public List<Comment> getComments() {
        return comments;
    }

    @Override
    public void addComment(Comment comment) {
        comments.add(comment);
    }
}
