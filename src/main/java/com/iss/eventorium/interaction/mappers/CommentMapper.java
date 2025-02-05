package com.iss.eventorium.interaction.mappers;

import com.iss.eventorium.interaction.dtos.comment.CommentResponseDto;
import com.iss.eventorium.interaction.dtos.comment.CreateCommentRequestDto;
import com.iss.eventorium.interaction.models.Comment;
import com.iss.eventorium.shared.dtos.CommentableResponseDto;
import com.iss.eventorium.shared.models.CommentableEntity;
import com.iss.eventorium.user.mappers.UserMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    private static ModelMapper modelMapper;

    @Autowired
    public CommentMapper(ModelMapper modelMapper) {
        CommentMapper.modelMapper = modelMapper;
    }

    public static Comment fromRequest(CreateCommentRequestDto request) {
        return modelMapper.map(request, Comment.class);
    }

    public static CommentResponseDto toResponse(Comment comment, CommentableEntity commentable) {
        CommentResponseDto dto = modelMapper.map(comment, CommentResponseDto.class);
        dto.setUser(UserMapper.toUserDetails(comment.getUser()));
        dto.setCommentable(new CommentableResponseDto(commentable.getId(), commentable.getDisplayName()));
        return dto;
    }
}
