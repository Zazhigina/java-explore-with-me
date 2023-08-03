package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    private static final String EWM_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        List<String> details = new ArrayList<>();
        for (ObjectError error : exception.getBindingResult().getAllErrors()) {
            details.add(error.getDefaultMessage());
        }
        log.warn("400 - Bad Request: {}", details);

        return new ApiError(HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                String.join(",", details),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(EWM_DATE_TIME_FORMAT))
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraintViolationException(final ConstraintViolationException exception) {
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        List<String> details = new ArrayList<>(violations.size());
        for (ConstraintViolation<?> violation : violations) {
            details.add(violation.getPropertyPath() + ": " + violation.getMessage());
        }

        log.warn("400 - Bad Request: {}", exception.getMessage());
        return new ApiError(HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                String.join(",", details),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(EWM_DATE_TIME_FORMAT))
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException exception) {
        log.warn("400 - Bad Request: {}", exception.getMessage());
        return new ApiError(HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                exception.getMessage(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(EWM_DATE_TIME_FORMAT))
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleHttpMessageNotReadableException(final HttpMessageNotReadableException exception) {
        log.warn("400 - Bad Request: {}", exception.getMessage());
        return new ApiError(HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                exception.getMessage(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(EWM_DATE_TIME_FORMAT))
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameterException(
            final MissingServletRequestParameterException exception) {
        log.warn("400 - Bad Request: {}", exception.getMessage());
        return new ApiError(HttpStatus.BAD_REQUEST,
                exception.getReason(),
                exception.getMessage(),
                exception.getTimestamp().format(DateTimeFormatter.ofPattern(EWM_DATE_TIME_FORMAT))
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(
            final BadRequestException exception) {
        log.warn("400 - Bad Request: {}", exception.getMessage());
        return new ApiError(HttpStatus.BAD_REQUEST,
                exception.getReason(),
                exception.getMessage(),
                exception.getTimestamp().format(DateTimeFormatter.ofPattern(EWM_DATE_TIME_FORMAT))
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleEntityNotFoundException(final EntityNotFoundException exception) {
        log.warn("404 - Not Found: {} - {}", exception.getMessage(), exception.getEntityClass());
        return new ApiError(HttpStatus.NOT_FOUND,
                exception.getReason(),
                exception.getMessage(),
                exception.getTimestamp().format(DateTimeFormatter.ofPattern(EWM_DATE_TIME_FORMAT))
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleEntityNotFoundException(final ValidationException exception) {
        log.warn("409 - Conflict: {}", exception.getMessage());
        return new ApiError(HttpStatus.CONFLICT,
                exception.getReason(),
                exception.getMessage(),
                exception.getTimestamp().format(DateTimeFormatter.ofPattern(EWM_DATE_TIME_FORMAT))
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(final DataIntegrityViolationException exception) {
        log.warn("409 - Conflict: {}", exception.getMessage());
        return new ApiError(HttpStatus.CONFLICT,
                "Integrity constraint has been violated.",
                exception.getMessage(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(EWM_DATE_TIME_FORMAT)));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleValidTimeException(final ValidTimeAndStatusException exception) {
        log.warn("409 - Conflict: {}", exception.getMessage());
        return new ApiError(HttpStatus.CONFLICT,
                exception.getReason(),
                exception.getMessage(),
                exception.getTimestamp().format(DateTimeFormatter.ofPattern(EWM_DATE_TIME_FORMAT))
        );
    }
}
