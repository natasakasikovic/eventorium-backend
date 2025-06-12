package com.iss.eventorium.interaction.mappers;

import com.iss.eventorium.interaction.dtos.comment.CommentResponseDto;
import com.iss.eventorium.interaction.dtos.comment.CreateCommentRequestDto;
import com.iss.eventorium.interaction.models.Comment;
import com.iss.eventorium.interaction.models.CommentType;
import com.iss.eventorium.user.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final ModelMapper modelMapper;
    private final UserMapper userMapper;

    public Comment fromRequest(CreateCommentRequestDto request) {
        return modelMapper.map(request, Comment.class);
    }

    public CommentResponseDto toResponse(Comment comment, String displayName) {
        CommentResponseDto dto = modelMapper.map(comment, CommentResponseDto.class);
        dto.setUser(userMapper.toUserDetails(comment.getAuthor()));
        dto.setDisplayName(displayName);
        return dto;
    }
}
