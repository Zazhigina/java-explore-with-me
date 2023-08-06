package ru.practicum.comment.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.NewCommentDto;
import ru.practicum.comment.model.UpdateCommentDto;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.enam.EventState.isStatePublished;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServerImpl implements CommentServer {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> getAllComments(Long userId, Long eventId) {
        List<Comment> comments = commentRepository.findAllByEventId(eventId);
        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto patchByUser(Long userId, Long commentId, UpdateCommentDto updateCommentDto) {
        User user = getUser(userId);
        Comment comment = getComment(commentId);
        validCommentUser(user, comment);
        if (updateCommentDto.getText() != null) {
            comment.setText(updateCommentDto.getText());
        }
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentUser(Long userId) {
        getUser(userId);
        List<Comment> commentList = commentRepository.findByAuthor_Id(userId);
        return commentList.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentEvent(Long userId, Long eventId) {
        User user = getUser(userId);
        Event event = getEvent(eventId);
        validEventUser(user, event);
        List<Comment> commentList = commentRepository.findAllByEventId(userId);
        return commentList.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto commentDto) {
        Event event = getEvent(eventId);
        User user = getUser(userId);
        if (!isStatePublished(event)) {
            log.debug("User with id {} can't add comments event not status PUBLISHED EventId id {}.", userId, eventId);
            throw new EntityNotFoundException(String.format("UserId id=%d can't add " +
                    "comments event not status PUBLISHED EventId id=%d.", userId, eventId), User.class, LocalDateTime.now());
        }
        return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(commentDto, event, user)));
    }


    private Event getEvent(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                String.format("Event with id=%d  was not found", id),
                Event.class,
                LocalDateTime.now()));
    }

    private User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                String.format("User with id=%d  was not found", id),
                User.class,
                LocalDateTime.now()));
    }

    private Comment getComment(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                String.format("Comment with id=%d  was not found", id),
                Comment.class,
                LocalDateTime.now()));
    }

    private void validCommentUser(User user, Comment comment) {
        if (!comment.getAuthor().equals(user)) {
            log.debug("User with id {} can't add is not the author of the commentary commentId id {}", user.getId(), comment.getId());
            throw new EntityNotFoundException(String.format("UserId id=%d can't add " +
                    "is not the author of the commentary commentId id=%d.", user.getId(), comment.getId()), User.class, LocalDateTime.now());
        }
    }

    private void validEventUser(User user, Event event) {
        if (!event.getInitiator().equals(user)) {
            log.debug("User with id {} is not the author of the event eventId id {}", user.getId(), event.getId());
            throw new EntityNotFoundException(String.format("UserId id=%d is not " +
                    "the author of the event eventId id=%d.", user.getId(), event.getId()), User.class, LocalDateTime.now());
        }
    }
}
