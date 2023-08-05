package ru.practicum.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
@Builder
public class ApiError {
    private HttpStatus status;
    private String reason;
    private String message;
    private String timestamp;

}
