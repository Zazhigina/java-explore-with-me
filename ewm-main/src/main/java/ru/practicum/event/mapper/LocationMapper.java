package ru.practicum.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.event.dto.LocationDto;
import ru.practicum.event.model.Location;

@UtilityClass
public class LocationMapper {
    public Location toLocation(LocationDto dto) {
        return Location.builder()
                .lon(dto.getLon())
                .lat(dto.getLat())
                .build();
    }
}
