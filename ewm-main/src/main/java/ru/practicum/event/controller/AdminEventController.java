package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.enam.EventState;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.model.update.UpdateEvent;
import ru.practicum.event.server.AdminEventService;
import ru.practicum.exception.BadRequestException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminEventController {
    private final AdminEventService adminEventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getEventsUser(@RequestParam(value = "users", required = false) List<Long> users,
                                            @RequestParam(value = "states", required = false) List<EventState> states,
                                            @RequestParam(value = "categories", required = false) List<Long> categories,
                                            @RequestParam(value = "rangeStart", required = false) String rangeStartString,
                                            @RequestParam(value = "rangeEnd", required = false) String rangeEndString,
                                            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) int from,
                                            @Positive @RequestParam(value = "size", defaultValue = "10", required = false) int size
    ) {
        log.info("GET запрос на поиск события.");
        LocalDateTime rangeStart = LocalDateTime.now().minusYears(20);
        LocalDateTime rangeEnd = LocalDateTime.now().plusYears(100);
        if (rangeStartString != null && rangeEndString != null) {
            rangeStart = decodeDate(rangeStartString);
            rangeEnd = decodeDate(rangeEndString);
        }
        return adminEventService.getEventFull(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    private LocalDateTime decodeDate(String dateString) {
        LocalDateTime rangeStart;
        String decodedRangeStart = URLDecoder.decode(dateString, StandardCharsets.UTF_8);
        rangeStart = LocalDateTime.parse(decodedRangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return rangeStart;
    }


    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto update(@PathVariable long eventId,
                               @Valid @RequestBody UpdateEvent request) {
        if (request.getEventDate() != null && request.getEventDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("The start date of the event to be changed must be no earlier than one hour from the publication date",
                    LocalDateTime.now());
        }
        log.info("PATCH запрос на редактирование данных события и его статуса (отклонение/публикация");
        return adminEventService.update(eventId, request);
    }
}
