package ru.practicum;

import lombok.*;

@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ViewStatsDto {
    private String app;
    private String uri;
    private Long hits;
}
