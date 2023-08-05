package ru.practicum.event.server.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.enam.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.enam.SortEvent;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.server.PublicEventService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.category.mapper.CategoryMapper.toCategoryDto;
import static ru.practicum.event.mapper.EventMapper.toEventFullDto;
import static ru.practicum.event.mapper.EventMapper.toEventShortDto;
import static ru.practicum.user.mapper.UserMapper.toUserShortDto;

@Service
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    private final EventRepository eventRepository;
    private final StatsClient statsClient;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<EventShortDto> getAll(String text, List<Long> categories,
                                      Boolean paid, String rangeStart,
                                      String rangeEnd, Boolean onlyAvailable,
                                      SortEvent sort, int from, int size) {
        LocalDateTime start;
        LocalDateTime end;
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        if (rangeStart == null || rangeEnd == null) {
            start = LocalDateTime.now();
            end = start.plusYears(100);
        } else {
            start = LocalDateTime.parse(rangeStart, formatter);
            end = LocalDateTime.parse(rangeEnd, formatter);
            if (end.isBefore(start) || start.isEqual(end)) {
                throw new BadRequestException(String.format("Start date=%s cannot be before end date=%s",
                        rangeStart, rangeEnd), LocalDateTime.now());
            }
        }

        if (text != null) text = text.toLowerCase();

        List<Event> result = new ArrayList<>();

        if (sort == null || sort.equals(SortEvent.EVENT_DATE)) {
            result = eventRepository.findByParametersForPublicSortEventDate(
                    text,
                    categories,
                    paid,
                    start,
                    end,
                    onlyAvailable,
                    pageable);
        } else {
            eventRepository.findByParametersForPublicSortViews(text,
                    categories,
                    paid,
                    start,
                    end,
                    onlyAvailable,
                    pageable);
        }

        Map<Long, Integer> hits = new HashMap<>();
        if (!result.isEmpty()) {
            hits = getStatsFromEvents(result);
        }
        List<EventShortDto> eventShortDtos = result.stream()
                .map(e -> toEventShortDto(e, toCategoryDto(e.getCategory()), toUserShortDto(e.getInitiator())))
                .collect(Collectors.toList());

        for (EventShortDto eventShortDto : eventShortDtos) {
            eventShortDto.setViews(hits.getOrDefault(eventShortDto.getId(), 0));
        }
        return eventShortDtos;
    }


    @Override
    @Transactional(readOnly = true)
    public EventFullDto getById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Event with id=%d  was not found", id),
                        Event.class,
                        LocalDateTime.now())
                );
        if (event.getState() != EventState.PUBLISHED) {
            throw new EntityNotFoundException(String.format("Event with id=%d not PUBLISHED", id), Event.class,
                    LocalDateTime.now());
        }
        EventFullDto eventFullDto = toEventFullDto(event,
                toCategoryDto(event.getCategory()),
                toUserShortDto(event.getInitiator()),
                event.getLocation());

        Map<Long, Integer> hits = getStatsFromEvents(List.of(event));
        eventFullDto.setViews(hits.get(id));
        return eventFullDto;

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
}
