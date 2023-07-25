package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto create(@Valid @RequestBody EndpointHitDto endPointHitDto) {
        log.info("POST запрос на cохранение информации.");
        return statsService.create(endPointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getAll(@RequestParam(value = "start") String start,
                                     @RequestParam(value = "end") String end,
                                     @RequestParam(value = "uris", defaultValue = "") List<String> uris,
                                     @RequestParam(value = "unique", defaultValue = "false") Boolean unique) {
        log.info("GET запрос на получение статистики по посещениям.");
        return statsService.getStats(start, end, uris, unique);
    }
}
