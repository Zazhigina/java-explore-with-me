package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.update.UpdateEvent;
import ru.practicum.event.server.PrivateEventService;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateEventController {
    private final PrivateEventService privateEventService;

    @GetMapping
    public List<EventShortDto> getEventsUser(@PathVariable long userId,
                                             @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
                                             @Positive @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        log.info("GET запрос на получение событий, добавленных текущим пользователем");
        return privateEventService.getEventsUser(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@RequestBody @Valid NewEventDto dto,
                               @PathVariable long userId) {
        log.info("POST запрос на добавление подборок событии.");
        return privateEventService.create(userId, dto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getById(@PathVariable long userId,
                                @PathVariable long eventId) {
        log.info("GET запрос на получение полной информации о событии добавленном текущим пользователем");
        return privateEventService.getById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid UpdateEvent dto) {
        log.info("PATCH запрос на  Изменение события добавленного текущим пользователем");
        return privateEventService.update(userId, eventId, dto);
    }


    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(@PathVariable Long userId,
                                                          @PathVariable Long eventId) {
        log.info("GET запрос на получение информации о запросах на участие в событии текущего пользователя");
        return privateEventService.getRequestsByEventIdAndUserId(userId, eventId);
    }


    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody EventRequestStatusUpdateRequest requestStatusUpdate) {
        log.info("PATCH запрос на Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя");
        return privateEventService.updateStatus(userId, eventId, requestStatusUpdate);

    }
}
