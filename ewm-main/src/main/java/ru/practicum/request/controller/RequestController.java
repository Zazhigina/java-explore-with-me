package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.server.RequestService;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(@PathVariable @Min(0) long userId,
                                          @RequestParam @Min(0) long eventId) {
        log.info("POST запрос на добавление запроса от текущего пользователя на участие в событии");
        return requestService.create(userId, eventId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getAllByUserId(@PathVariable @Min(0) long userId) {
        log.info("GET запрос на получение информации о заявках текущего пользователя на участие в чужих событиях");
        return requestService.getAllByUserId(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequestByRequestor(@PathVariable long userId,
                                                            @PathVariable long requestId) {
        log.info("PATCH запрос на отмену своего запроса на участие в событии");
        return requestService.cancelRequestByRequestor(userId, requestId);
    }
}
