package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatsClient;
import ru.practicum.enam.SortEvent;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.server.PublicEventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicEventController {
    private final PublicEventService publicEventService;
    private final StatsClient statsClient;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAll(@RequestParam(name = "text", required = false) String text,
                                      @RequestParam(name = "categories", required = false) List<Long> categories,
                                      @RequestParam(name = "paid", required = false) Boolean paid,
                                      @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                      @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                      @RequestParam(name = "onlyAvailable", defaultValue = "false") boolean onlyAvailable,
                                      @RequestParam(name = "sort", required = false) SortEvent sort,
                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                      @Positive @RequestParam(name = "size", defaultValue = "10") int size,
                                      HttpServletRequest request) {
        log.info("GET запрос на получение событий с возможностью фильтрации");
        statsClient.createHit(request);
        return publicEventService.getAll(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getById(
            @PathVariable long id, HttpServletRequest request) {
        log.info("GET запрос на получение подробной информации об опубликованном событии по его идентификатору");
        statsClient.createHit(request);
        return publicEventService.getById(id);
    }
}
