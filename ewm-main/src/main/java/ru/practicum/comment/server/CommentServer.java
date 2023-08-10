package ru.practicum.comment.server;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.model.NewCommentDto;
import ru.practicum.comment.model.UpdateCommentDto;

import java.util.List;

public interface CommentServer {
    CommentDto createComment(Long userId, Long eventId, NewCommentDto commentDto);

    CommentDto patchByUser(Long userId, Long commentId, UpdateCommentDto updateCommentDto);

    List<CommentDto> getCommentUser(Long userId);

    List<CommentDto> getCommentEvent(Long userId, Long eventId);


}
