package com.iss.eventorium.interaction.mappers;

import com.iss.eventorium.interaction.dtos.comment.CommentResponseDto;
import com.iss.eventorium.interaction.dtos.comment.CreateCommentRequestDto;
import com.iss.eventorium.interaction.models.Comment;
import com.iss.eventorium.interaction.models.CommentType;
import com.iss.eventorium.shared.dtos.CommentableResponseDto;
import com.iss.eventorium.shared.models.CommentableEntity;
import com.iss.eventorium.user.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final ModelMapper modelMapper;
    private final UserMapper userMapper;

    public Comment fromRequest(CreateCommentRequestDto request, CommentType type, Long id) {
        Comment comment = modelMapper.map(request, Comment.class);
        comment.setCommentType(type);
        comment.setCommentableId(id);
        return comment;
    }

    public CommentResponseDto toResponse(Comment comment, CommentableEntity commentable) {
        CommentResponseDto dto = modelMapper.map(comment, CommentResponseDto.class);
        dto.setUser(userMapper.toUserDetails(comment.getUser()));
        dto.setCommentable(new CommentableResponseDto(commentable.getId(), commentable.getDisplayName()));
        return dto;
    }
}
