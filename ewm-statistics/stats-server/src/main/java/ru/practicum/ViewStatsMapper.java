package ru.practicum;

import lombok.experimental.UtilityClass;
import ru.practicum.model.ViewStats;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ViewStatsMapper {
    public static ViewStatsDto toViewStatDto(ViewStats viewStat) {
        return ViewStatsDto.builder()
                .app(viewStat.getApp())
                .uri(viewStat.getUri())
                .hits(viewStat.getHits())
                .build();
    }

    public static List<ViewStatsDto> mapToViewStatsDto(List<ViewStats> stats) {
        List<ViewStatsDto> result = new ArrayList<>();
        for (ViewStats stat : stats) {
            result.add(toViewStatDto(stat));
        }
        return result;
    }
}
