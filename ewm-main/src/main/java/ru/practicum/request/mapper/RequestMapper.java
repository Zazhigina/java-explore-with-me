package ru.practicum.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class RequestMapper {


    public ParticipationRequestDto toParticipationRequestDto(Request request) {

        return ParticipationRequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .created(request.getCreated())
                .build();
    }

    public List<ParticipationRequestDto> toParticipationRequestDtoCollection(
            Collection<Request> requests) {
        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }
}
