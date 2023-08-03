package ru.practicum.compliiation.server;

import ru.practicum.compliiation.dto.CompilationDto;

import java.util.List;

public interface PublicCompilationService {
    CompilationDto getById(Long compId);

    List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size);
}
