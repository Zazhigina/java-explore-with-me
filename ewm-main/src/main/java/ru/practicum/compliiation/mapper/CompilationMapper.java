package ru.practicum.compliiation.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.compliiation.dto.CompilationDto;
import ru.practicum.compliiation.dto.NewCompilationDto;
import ru.practicum.compliiation.model.Compilation;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;

import java.util.List;
import java.util.Set;

@UtilityClass
public class CompilationMapper {
    public CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> events) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(events)
                .build();
    }

    public Compilation toCompilation(NewCompilationDto newCompilationDto, Set<Event> events) {
        return Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.getPinned())
                .events(events)
                .build();
    }
}
