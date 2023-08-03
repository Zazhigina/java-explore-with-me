package ru.practicum.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class EntityNotFoundException extends RuntimeException {


    private final String reason = "The required object was not found.";
    private Class<?> entityClass;
    private LocalDateTime timestamp;


    public EntityNotFoundException(String message, Class<?> entityClass, LocalDateTime timestamp) {
        super(message);
        this.entityClass = entityClass;
        this.timestamp = timestamp;
    }
}
