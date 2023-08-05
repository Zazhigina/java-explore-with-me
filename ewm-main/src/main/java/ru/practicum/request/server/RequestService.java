package ru.practicum.request.server;

import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    ParticipationRequestDto create(Long userId, Long eventId);

    List<ParticipationRequestDto> getAllByUserId(Long userId);

    ParticipationRequestDto cancelRequestByRequestor(Long userId, Long requestId);
}
