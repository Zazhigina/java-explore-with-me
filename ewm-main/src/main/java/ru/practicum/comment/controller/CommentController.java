package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.model.NewCommentDto;
import ru.practicum.comment.model.UpdateCommentDto;
import ru.practicum.comment.server.CommentServer;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CommentController {
    private final CommentServer commentServer;

    @PostMapping("/events/{eventId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable Long userId, @PathVariable Long eventId,
                                 @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("POST запрос на добавление комментария: {}", newCommentDto);
        return commentServer.createComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/comment/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto patchRequestByUser(@PathVariable Long userId, @PathVariable Long commentId,
                                         @Valid @RequestBody UpdateCommentDto updateCommentDto) {

        log.info("PATCH запрос на обновление пользователем с userId = {}  комментария с commentId = {} " +
                "для события requestStatusUpdateDto = {}", userId, commentId, updateCommentDto);

        return commentServer.patchByUser(userId, commentId, updateCommentDto);
    }

    @GetMapping("/comment")
    public List<CommentDto> getRequestListUser(@PathVariable Long userId) {
        log.info("GET запрос на получение комментариев пользователя с userId = {} ", userId);
        return commentServer.getCommentUser(userId);
    }

    @GetMapping("/events/{eventId}/comment")
    public List<CommentDto> getRequestListAllCommentsEvent(@PathVariable Long eventId,
                                                           @PathVariable Long userId) {
        log.info("GET запрос на получение всех комментариев своего события с eventId = {} ", eventId);
        return commentServer.getCommentEvent(userId, eventId);
    }
}

