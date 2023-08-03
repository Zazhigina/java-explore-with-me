package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Service
@Slf4j
public class StatsClient extends BaseClient {
    private static final String APPLICATION_NAME = "ewm-main-service";

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createHit(HttpServletRequest request) {
        final EndpointHitDto hit = EndpointHitDto.builder()
                .app(APPLICATION_NAME)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(Timestamp.from(Instant.now()).toLocalDateTime())
                .build();
        return post("/hit", hit);
    }

    public List<ViewStatsDto> getHit(String start, String end, List<String> uris, Boolean unique) {
        String queryString = "?start={start}&end={end}&uris={uris}&unique={unique}";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start);
        parameters.put("end", end);
        if (uris != null) {
            parameters.put("uris", uris);
        }
        if (unique) {
            parameters.put("unique", true);
        }
        ResponseEntity<ViewStatsDto[]> responseEntity = get("/stats" + queryString, null, parameters);

        ViewStatsDto[] stats = responseEntity.getBody();

        if (stats != null && stats.length > 0) {
            return Arrays.asList(stats);
        }
        return new ArrayList<>();
    }
}

