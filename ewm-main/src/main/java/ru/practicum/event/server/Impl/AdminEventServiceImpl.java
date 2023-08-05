package ru.practicum.event.server.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.enam.EventState;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.update.UpdateEvent;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.event.server.AdminEventService;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.ValidTimeAndStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.category.mapper.CategoryMapper.toCategoryDto;
import static ru.practicum.enam.EventState.isStatePending;
import static ru.practicum.enam.EventState.isStatePendingOrCancelled;
import static ru.practicum.event.mapper.EventMapper.toEventFullDto;
import static ru.practicum.user.mapper.UserMapper.toUserShortDto;

@Service
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {
    private final EventRepository repository;
    private final LocationRepository locationRepository;
    private final StatsClient statsClient;
    private static final int MINIMUM_HOURS_FOR_UPDATE_EVENT = 1;



    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEventFull(List<Long> users, List<EventState> states, List<Long> categories,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (rangeEnd.isBefore(rangeStart)) {
            throw new BadRequestException(String.format(
                    "Start date=%s cannot be before end date=%s", rangeStart, rangeEnd), LocalDateTime.now());
        }
        List<Event> eventList = repository.findByParameters(users, states, categories,
                rangeStart, rangeEnd, pageable);
        if (eventList.isEmpty()) return Collections.emptyList();

        List<Long> eventIdList = new ArrayList<>();
        for (Event event : eventList) {
            eventIdList.add(event.getId());
        }

        return eventList.stream()
                .map(e -> toEventFullDto(e,
                        toCategoryDto(e.getCategory()),
                        toUserShortDto(e.getInitiator()),
                        e.getLocation()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto update(Long eventId, UpdateEvent request) {
        Event recipient = getEvent(eventId);

        if (!isStatePending(recipient) || !isStatePendingOrCancelled(recipient))
            throw new ValidTimeAndStatusException("Event not pending", LocalDateTime.now());

        if (recipient.getEventDate().isBefore(LocalDateTime.now().plusHours(MINIMUM_HOURS_FOR_UPDATE_EVENT))) {
            throw new ValidTimeAndStatusException(
                    "The start date of the event to be changed must be no earlier than one hour from the publication date",
                    LocalDateTime.now());
        }

        Event eventNew = updateEvent(recipient, EventMapper.updateEventAdmin(request));
        Map<Long, Integer> hits = getStatsFromEvent(eventNew);
        eventNew.setViews(hits.getOrDefault(eventNew.getId(), 0));


        Event savedEvent = repository.save(eventNew);
        return toEventFullDto(savedEvent,
                toCategoryDto(savedEvent.getCategory()),
                toUserShortDto(savedEvent.getInitiator()),
                savedEvent.getLocation());
    }

    private Event getEvent(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Event with id=%d  was not found", id),
                        Event.class,
                        LocalDateTime.now())
                );
    }

    private Event updateEvent(Event request, Event recipient) {
        if (recipient.getLocation() != null) {
            Location location = getLocation(recipient.getLocation());
            request.setLocation(location);
        }
        return EventMapper.updateEvent(request, recipient);
    }

    private Location getLocation(Location location) {
        return locationRepository.findByLatAndLon(location.getLat(),
                location.getLon()).orElse(locationRepository.save(location));
    }

    private Map<Long, Integer> getStatsFromEvent(Event events) {
        Map<Long, Integer> hits = new HashMap<>();

        List<Long> eventIds = Collections.singletonList(events.getId());

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
}
