package ru.practicum.event.server;

import ru.practicum.enam.SortEvent;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;

import java.time.LocalDateTime;
import java.util.List;

public interface PublicEventService {
    List<EventShortDto> getAll(String text, List<Long> categories, Boolean paid, String rangeStart,
                               String rangeEnd, Boolean onlyAvailable, SortEvent sort,
                               int from, int size);

    EventFullDto getById(Long id);
}
