package ru.practicum.comment.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.NewCommentDto;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

@UtilityClass
public class CommentMapper {
    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .lastUpdatedOn(comment.getLastUpdatedOn())
                .build();
    }

    public Comment toComment(NewCommentDto commentDto, Event event, User user) {
        return Comment.builder()
                .text(commentDto.getText())
                .event(event)
                .author(user)
                .build();
    }
}