package ru.practicum.event.server;

import ru.practicum.enam.EventState;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.model.update.UpdateEvent;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventService {
    List<EventFullDto> getEventFull(List<Long> users, List<EventState> states, List<Long> categories,
                                    LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    EventFullDto update(Long eventId, UpdateEvent request);
}
