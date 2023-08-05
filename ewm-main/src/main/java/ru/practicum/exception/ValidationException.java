package ru.practicum.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ValidationException extends RuntimeException {
    private final String reason = "For the requested operation the conditions are not met.";
    private LocalDateTime timestamp;

    public ValidationException(String message, LocalDateTime timestamp) {
        super(message);
        this.timestamp = timestamp;
    }
}
