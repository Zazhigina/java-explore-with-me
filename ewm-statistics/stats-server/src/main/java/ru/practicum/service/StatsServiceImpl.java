package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.EndpointHitMapper;
import ru.practicum.ViewStatsDto;
import ru.practicum.ViewStatsMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatsRepository;
import ru.practicum.exception.TimeParamsException;


import javax.transaction.Transactional;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {

    public final StatsRepository statsRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public EndpointHitDto create(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = EndpointHitMapper.toEndpointHit(endpointHitDto);
        return EndpointHitMapper.toEndpointHitDto(statsRepository.save(endpointHit));
    }

    @Override
    @Transactional
    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime startDate = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), FORMATTER);
        LocalDateTime endDate = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), FORMATTER);
        if (startDate.isAfter(endDate)) {
            throw new TimeParamsException("Неверный запрос - проверьте параметры start и end, вероятно, start находится после end");
        }
        if (uris == null || uris.isEmpty()) {
            if (unique) {
                return ViewStatsMapper.mapToViewStatsDto(statsRepository.getAllStatsDistinctIp(startDate, endDate));
            } else {
                return ViewStatsMapper.mapToViewStatsDto(statsRepository.getAllStats(startDate, endDate));
            }
        } else {
            if (unique) {
                return ViewStatsMapper.mapToViewStatsDto(statsRepository.getStatsByUrisDistinctIp(startDate, endDate, uris));
            } else {
                return ViewStatsMapper.mapToViewStatsDto(statsRepository.getStatsByUris(startDate, endDate, uris));
            }
        }
    }
}
