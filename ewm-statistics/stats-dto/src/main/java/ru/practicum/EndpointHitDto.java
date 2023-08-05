package ru.practicum;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHitDto {
    private Long id;
    @NotBlank(message = "App не должен быть пустым")
    private String app;
    @NotBlank(message = "Uri не должен быть пустым")
    private String uri;
    @NotBlank(message = "Ip не должен быть пустым")
    private String ip;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "Timestamp cannot be empty")
    private LocalDateTime timestamp;
}
