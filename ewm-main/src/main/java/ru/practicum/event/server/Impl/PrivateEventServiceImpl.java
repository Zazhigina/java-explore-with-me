package ru.practicum.event.server.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.category.model.Category;
import ru.practicum.enam.EventState;
import ru.practicum.enam.RequestStatus;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.LocationDto;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.update.UpdateEvent;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.event.server.ValidClass;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.server.PrivateEventService;
import ru.practicum.exception.ValidTimeAndStatusException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.category.mapper.CategoryMapper.toCategoryDto;
import static ru.practicum.event.mapper.EventMapper.*;
import static ru.practicum.event.mapper.LocationMapper.toLocation;
import static ru.practicum.request.mapper.RequestMapper.toParticipationRequestDto;
import static ru.practicum.user.mapper.UserMapper.toUserShortDto;


@Service
@RequiredArgsConstructor
public class PrivateEventServiceImpl implements PrivateEventService {
    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final RequestRepository requestRepository;

    private final LocationRepository locationRepository;

    private final ValidClass validClass;

    private final StatsClient statsClient;

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsUser(Long userId, Integer from, Integer size) {
        getUser(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiator_Id(userId, pageable);
        if (events.isEmpty()) return Collections.emptyList();
        return events.stream().map(e -> toEventShortDto(e, toCategoryDto(e.getCategory()), toUserShortDto(e.getInitiator()))).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto create(Long userId, NewEventDto dto) {
        User user = getUser(userId);
        Category category = getCategory(dto.getCategory());
        Location location = saveLocation(dto.getLocation());
        validClass.eventTimeCheck(dto.getEventDate());
        Event event = toEvent(dto, category, location, user);

        Event savedEvent = eventRepository.save(event);
        return toEventFullDto(savedEvent, toCategoryDto(savedEvent.getCategory()), toUserShortDto(savedEvent.getInitiator()), savedEvent.getLocation());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getById(Long userId, Long eventId) {
        User user = getUser(userId);
        Event event = getEvent(eventId);
        Category category = getCategory(event.getCategory().getId());
        Location location = event.getLocation();
        validateEventInitiator(eventId, userId);
        Map<Long, Integer> hits = getStatsFromEvents(List.of(event));

        EventFullDto eventFullDto = toEventFullDto(event,
                toCategoryDto(category),
                toUserShortDto(user),
                location);

        eventFullDto.setViews(Math.toIntExact(hits.getOrDefault(eventId, 0)));

        return eventFullDto;
    }

    @Override
    @Transactional
    public EventFullDto update(Long userId, Long eventId, UpdateEvent dto) {
        User user = getUser(userId);
        Event event = getEvent(eventId);
        validateBeforeUpdate(event, user);
        validUpdateEventParams(dto);
        Event eventRequest = EventMapper.updateEventUser(dto);
        Event eventNew = EventMapper.updateEvent(event, eventRequest);
        Event updatedEvent = eventRepository.save(eventNew);
        Map<Long, Integer> hits = getStatsFromEvents(List.of(event));

        EventFullDto eventFullDto = toEventFullDto(updatedEvent,
                toCategoryDto(updatedEvent.getCategory()),
                toUserShortDto(updatedEvent.getInitiator()),
                updatedEvent.getLocation());

        eventFullDto.setViews(hits.getOrDefault(eventId, 0));

        return eventFullDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsByEventIdAndUserId(Long userId, Long eventId) {
        validateEventInitiator(eventId, userId);
        List<Request> requests = requestRepository.findByEventInitiatorIdAndEventId(userId, eventId);
        if (requests.isEmpty()) return Collections.emptyList();
        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest requestStatusUpdate) {
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Event with id=%s was not found", eventId), Event.class, LocalDateTime.now()));

        if (event.getParticipants().size() >= event.getParticipantLimit()) {
            throw new ValidationException("The participant limit has been reached", LocalDateTime.now());
        }


        if (event.getState() != EventState.PUBLISHED) {
            throw new ValidationException("You can't participate in an unpublished event", LocalDateTime.now());
        }

        List<Request> requests = requestRepository.findAllById(requestStatusUpdate.getRequestIds());

        requests.forEach(r -> {
            if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
                return;
            }
            if (RequestStatus.REJECTED == requestStatusUpdate.getStatus()) {
                r.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(toParticipationRequestDto(r));
            }
            if (RequestStatus.CONFIRMED == requestStatusUpdate.getStatus()) {
                r.setStatus(RequestStatus.CONFIRMED);
                confirmedRequests.add(toParticipationRequestDto(r));
            }
        });

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }

    private EventRequestStatusUpdateResult prepareRequestStatusUpdateResult(List<Request> requests, RequestStatus newStatus, Event event) {
        EventRequestStatusUpdateResult result;
        switch (newStatus) {
            case CONFIRMED:
                result = confirmRequests(requests, event);
                break;
            case REJECTED:
                result = rejectRequests(requests);
                break;
            default:
                throw new ValidationException(String.format("Status %s not supported", newStatus), LocalDateTime.now());
        }
        return result;
    }

    private Event getEvent(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d  was not found", id), Event.class, LocalDateTime.now()));
    }

    private User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("User with id=%d  was not found", id), User.class, LocalDateTime.now()));
    }

    private Category getCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Category with id=%d was not found", id), Category.class, LocalDateTime.now()));
    }

    private Location saveLocation(LocationDto dto) {
        Location location = toLocation(dto);
        return locationRepository.save(location);
    }

    private List<Request> getRequestsByIds(List<Long> requestIds) {
        return requestRepository.getAllByIdIn(requestIds);
    }

    private EventRequestStatusUpdateResult confirmRequests(List<Request> requests, Event event) {
        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();

        try {
            for (Request request : requests) {
                if (event.getParticipants().size() < event.getParticipantLimit()) {
                    request.setStatus(RequestStatus.CONFIRMED);
                    confirmedRequests.add(request);
                    event.getParticipants().add(event.getInitiator());
                } else {
                    request.setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(request);
                }
            }
            if (!rejectedRequests.isEmpty()) {
                throw new ValidationException();
            }
        } catch (ValidationException exception) {
            eventRepository.flush();
            requestRepository.flush();
            throw new ValidationException("The participant limit has been reached", LocalDateTime.now());
        }

        eventRepository.flush();
        requestRepository.flush();

        List<ParticipationRequestDto> confirmedRequestsDto = confirmedRequests.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());

        return new EventRequestStatusUpdateResult(confirmedRequestsDto, Collections.emptyList());
    }

    private EventRequestStatusUpdateResult rejectRequests(List<Request> requests) {
        for (Request request : requests) {
            request.setStatus(RequestStatus.REJECTED);
        }
        requestRepository.flush();

        List<ParticipationRequestDto> rejectedRequests = requests.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());

        return new EventRequestStatusUpdateResult(Collections.emptyList(), rejectedRequests);
    }

    private void validateEventInitiator(Long eventId, Long userId) {
        User user = getUser(userId);
        Event event = getEvent(eventId);
    }

    private void validateEvent(Event event, User user) {
        if (!Objects.equals(event.getInitiator().getId(), user.getId())) {
            throw new EntityNotFoundException(String.format("Event with id=%d  was not found", event.getId()), Event.class, LocalDateTime.now());
        }
    }

    private void validateBeforeUpdate(Event event, User user) {
        if (!EventState.isStatePendingOrCancelled(event)) {
            throw new ValidTimeAndStatusException("Only pending or canceled events can be changed", LocalDateTime.now());
        }
        validClass.eventTimeCheck(event.getEventDate());
        validateEvent(event, user);
    }

    private void validateBeforeRequestStatusUpdate(Event event, User user) {
        if (isParticipantLimitZeroAndNoModerationRequired(event)) {
            throw new ValidationException("Validation not required for this event.", LocalDateTime.now());
        }
        validateEvent(event, user);
    }

    private boolean isParticipantLimitZeroAndNoModerationRequired(Event event) {
        return event.getParticipantLimit() == 0 && !event.getRequestModeration();
    }

    private List<Request> processRequestsForStatusUpdate(List<Long> requestIds, Long eventId) {
        List<Request> requests = getRequestsByIds(requestIds);

        Optional<List<Request>> reqOp = Optional.of(requests.stream()
                .filter(request -> Objects.equals(request.getEvent().getId(), eventId))
                .filter(request -> RequestStatus.PENDING.equals(request.getStatus()))
                .collect(Collectors.toList()));
        return reqOp.orElseThrow(() ->
                new ValidationException("Request must be in PENDING status.", LocalDateTime.now()));
    }

    private Map<Long, Integer> getStatsFromEvents(List<Event> events) {
        Map<Long, Integer> hits = new HashMap<>();

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        List<String> uris = eventIds.stream()
                .map(i -> "/events/" + i)
                .collect(Collectors.toList());

        String start = LocalDateTime.now().minusYears(50).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String end = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        List<ViewStatsDto> viewStatDtos = statsClient.getHit(start, end, uris, true);

        for (ViewStatsDto viewStatDto : viewStatDtos) {
            String uri = viewStatDto.getUri();
            hits.put(Long.parseLong(uri.substring(8)), Math.toIntExact(viewStatDto.getHits()));
        }
        return hits;
    }


    private void validUpdateEventParams(UpdateEvent updateEventRequest) {
        if (!Objects.isNull(updateEventRequest.getAnnotation())) {
            if (updateEventRequest.getAnnotation().length() < 20 ||
                    updateEventRequest.getAnnotation().length() > 2000) {
                throw new BadRequestException("Annotation length cannot be less than 20 or more than 2000", LocalDateTime.now());
            }
        }

        if (!Objects.isNull(updateEventRequest.getCategory())) {
            getCategory(updateEventRequest.getCategory());
        }

        if (!Objects.isNull(updateEventRequest.getDescription())) {
            if (updateEventRequest.getDescription().length() < 20 ||
                    updateEventRequest.getDescription().length() > 7000) {
                throw new BadRequestException("Description length cannot be less than 20 or more than 7000", LocalDateTime.now());
            }
        }

        if (!Objects.isNull(updateEventRequest.getEventDate())) {
            validClass.eventTimeCheck(updateEventRequest.getEventDate());
        }

        if (!Objects.isNull(updateEventRequest.getLocation())) {
            getLocationOrAddNew(updateEventRequest.getLocation());
        }

        if (!Objects.isNull(updateEventRequest.getTitle())) {
            if (updateEventRequest.getTitle().length() < 3 ||
                    updateEventRequest.getTitle().length() > 120) {
                throw new BadRequestException("Title length cannot be less than 3 or more than 120", LocalDateTime.now());
            }
        }
    }

    private void getLocationOrAddNew(Location loc) {
        Optional<Location> location = locationRepository.findByLatAndLon(
                loc.getLat(),
                loc.getLon());
        if (location.isEmpty()) {
            locationRepository.save(location.get());
        }
    }
}

