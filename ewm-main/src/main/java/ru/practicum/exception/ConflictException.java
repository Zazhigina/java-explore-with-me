package ru.practicum.exception;

import java.time.LocalDateTime;

public class ConflictException extends RuntimeException {
    private final String reason = "Integrity constraint has been violated";
    private LocalDateTime timestamp;

    public ConflictException(String message, LocalDateTime timestamp) {
        super(message);
        this.timestamp = timestamp;
    }
}