package ru.practicum.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class MissingServletRequestParameterException extends RuntimeException {
    private final String reason = "Incorrectly made request";
    private LocalDateTime timestamp;


    public MissingServletRequestParameterException(String message, LocalDateTime timestamp) {
        super(message);
        this.timestamp = timestamp;
    }
}
