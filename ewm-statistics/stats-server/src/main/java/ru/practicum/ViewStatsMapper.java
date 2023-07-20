package ru.practicum;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ViewStatsMapper {
    public static ViewStatsDto toViewStatDto(ru.practicum.model.ViewStats viewStat) {
        return ViewStatsDto.builder()
                .app(viewStat.getApp())
                .uri(viewStat.getUri())
                .hits(viewStat.getHits())
                .build();
    }

    public static List<ViewStatsDto> mapToViewStatsDto(Iterable<ru.practicum.model.ViewStats> stats) {
        List<ViewStatsDto> result = new ArrayList<>();
        for (ru.practicum.model.ViewStats stat : stats) {
            result.add(toViewStatDto(stat));
        }
        return result;
    }
}
