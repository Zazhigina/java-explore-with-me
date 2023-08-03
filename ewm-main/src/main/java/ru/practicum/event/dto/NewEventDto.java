package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {
    @NotBlank(message = "annotation cannot be empty or null.")
    @Size(min = 20, max = 2000, message = "annotation cannot be less than 20 or more than 2000.")
    private String annotation;

    @NotNull(message = "category id cannot be null.")
    @Positive(message = "category id cannot be negative or zero.")
    private Long category;

    @NotBlank(message = "description be empty or null.")
    @Size(min = 20, max = 7000, message = "description cannot be less than 20 or more than 7000.")
    private String description;

    @NotNull(message = "event date cannot be null.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull(message = "location cannot be null.")
    private LocationDto location;

    @Builder.Default
    private Boolean paid = false;

    @Builder.Default
    @PositiveOrZero(message = "participant limit cannot be negative.")
    private Integer participantLimit = 0;

    @Builder.Default
    private Boolean requestModeration = true;

    @NotBlank(message = "title cannot be empty or null.")
    @Size(min = 3, max = 120, message = "title cannot be less than 3 or more than 120.")
    private String title;
}
