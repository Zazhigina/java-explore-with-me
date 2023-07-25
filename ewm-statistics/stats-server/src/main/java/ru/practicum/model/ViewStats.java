package ru.practicum.model;

import lombok.*;

@AllArgsConstructor
@Data
@Builder
public class ViewStats {
    private String app;
    private String uri;
    private Long hits;
}
