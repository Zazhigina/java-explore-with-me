package ru.practicum.event.model.update;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.enam.StateAction;
import ru.practicum.event.model.Location;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class UpdateEvent {

    @Size(min = 3, max = 120, message = "Field: title. Size must be between {min} and {max}.")
    private String title;

    @Size(min = 20, max = 2000, message = "Field: annotation. Size must be between {min} and {max}.")
    private String annotation;

    @Positive(message = "category id cannot be negative or zero.")
    private Long category;

    private Boolean paid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @Size(min = 20, max = 7000, message = "Field: description. Size must be between {min} and {max}.")
    private String description;

    @PositiveOrZero(message = "participant limit cannot be negative.")
    private Integer participantLimit;

    @Valid
    private Location location;

    private Boolean requestModeration;

    private StateAction stateAction;

}