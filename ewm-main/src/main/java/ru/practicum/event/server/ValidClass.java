package ru.practicum.event.server;

import org.springframework.stereotype.Component;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ValidTimeAndStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ValidClass {
    private static final String FORMATTER = "yyyy-MM-dd HH:mm:ss";
    private static final int MINIMUM_HOURS_BEFORE_EVENT = 2;

    public void eventTimeCheck(LocalDateTime timeEvent) {
        if (timeEvent.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Field: eventDate. " +
                    "Error: должно содержать дату, которая еще не наступила. " +
                    "Value: " + timeEvent.format(DateTimeFormatter.ofPattern(FORMATTER)),
                    LocalDateTime.now());
        }
        if (timeEvent.isBefore(LocalDateTime.now().plusHours(MINIMUM_HOURS_BEFORE_EVENT))) {
            throw new BadRequestException("Field: eventDate. " +
                    "Error: событие должно начаться как минимум через два часа. " +
                    "Value: " + timeEvent.format(DateTimeFormatter.ofPattern(FORMATTER)),
                    LocalDateTime.now());
        }
    }
}
