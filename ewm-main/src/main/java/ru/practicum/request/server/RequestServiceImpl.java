package ru.practicum.request.server;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.enam.EventState;
import ru.practicum.enam.RequestStatus;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static ru.practicum.request.mapper.RequestMapper.toParticipationRequestDto;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public ParticipationRequestDto create(Long userId, Long eventId) {
        User user = getUser(userId);
        Event event = getEvent(eventId);
        validateBeforeCreate(user, event);
        Request requestExist = requestRepository.findOneByEventIdAndRequesterId(eventId, userId);

        if (!Objects.isNull(requestExist)) {
            throw new ValidationException(String.format("Event with id=%s and requester with id=%s already exist",
                    eventId, userId), LocalDateTime.now());
        }

        Request request = Request.builder()
                .requester(user)
                .event(event)
                .created(LocalDateTime.now())
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        return toParticipationRequestDto(requestRepository.save(request));
    }


    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getAllByUserId(Long userId) {
        getUser(userId);
        List<Request> request = requestRepository.findAllByRequester_Id(userId);
        if (request.isEmpty()) return Collections.emptyList();
        return RequestMapper.toParticipationRequestDtoCollection(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequestByRequestor(Long userId, Long requestId) {
        User user = getUser(userId);
        Request request = getRequest(requestId);
        validateBeforeCancel(request, user);
        removeFromEvent(request);
        setStatusCancel(request);
        return toParticipationRequestDto(request);
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("User with id=%d was not found", id),
                        User.class,
                        LocalDateTime.now())
                );
    }

    private Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Event with id=%d  was not found", id),
                        Event.class,
                        LocalDateTime.now())
                );
    }

    private Request getRequest(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Request with id=%d was not found", id),
                        Request.class,
                        LocalDateTime.now())
                );
    }

    private void validateBeforeCreate(User user, Event event) {
        validateEventIsPublished(event);
        validateUserIsNotInitiator(user, event);
        validateParticipantLimit(event);
    }

    private void validateBeforeCancel(Request request, User user) {
        validateStatusNotCanceled(request);
        validateUserIsRequestor(request, user);
    }

    private void validateStatusNotCanceled(Request request) {
        if (RequestStatus.CANCELED.equals(request.getStatus())) {
            throw new ValidationException(
                    String.format("Request id=%d already cancelled", request.getId()),
                    LocalDateTime.now()
            );
        }
    }

    private void validateUserIsRequestor(Request request, User user) {
        if (!user.getId().equals(request.getRequester().getId())) {
            throw new EntityNotFoundException(
                    String.format("Request with id=%d was not found", request.getId()),
                    Request.class,
                    LocalDateTime.now()
            );
        }
    }

    private void validateUserIsNotInitiator(User user, Event event) {
        if (user.getId().equals(event.getInitiator().getId())) {
            throw new ValidationException(String.format("User id=%d initiated this event", user.getId()),
                    LocalDateTime.now());
        }
    }

    private void validateEventIsPublished(Event event) {
        if (!EventState.isStatePublished(event)) {
            throw new ValidationException("This event is not published. " +
                    "Event State is " + event.getState(),
                    LocalDateTime.now());
        }
    }

    private void validateParticipantLimit(Event event) {
        if (event.getParticipantLimit() != 0 &&
                event.getParticipants().size() >= event.getParticipantLimit()) {
            throw new ValidationException(
                    String.format("Exceeded the number of event participants: %d", event.getParticipantLimit()),
                    LocalDateTime.now()
            );
        }
    }

    private void setStatusCancel(Request request) {
        request.setStatus(RequestStatus.CANCELED);
        requestRepository.flush();
    }

    private void removeFromEvent(Request request) {
        if (!RequestStatus.CONFIRMED.equals(request.getStatus())) return;
        Event event = request.getEvent();
        event.getParticipants().remove(event.getInitiator());
        eventRepository.flush();
    }
}
