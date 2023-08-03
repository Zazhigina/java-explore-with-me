package ru.practicum.event.server;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.update.UpdateEvent;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;


import java.util.List;

public interface PrivateEventService {
    List<EventShortDto> getEventsUser(Long userId, Integer from, Integer size);

    EventFullDto create(Long userId, NewEventDto dto);

    EventFullDto getById(Long userId, Long eventId);

    EventFullDto update(Long userId, Long eventId, UpdateEvent dto);


    List<ParticipationRequestDto> getRequestsByEventIdAndUserId(Long userId, Long eventId);


    EventRequestStatusUpdateResult updateStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest requestStatusUpdate);

}