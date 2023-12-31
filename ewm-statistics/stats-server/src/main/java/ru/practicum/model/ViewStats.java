package ru.practicum.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ViewStats {
    private String app;
    private String uri;
    private Long hits;
}
