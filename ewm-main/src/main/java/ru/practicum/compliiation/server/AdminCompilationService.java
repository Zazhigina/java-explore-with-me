package ru.practicum.compliiation.server;

import ru.practicum.compliiation.dto.CompilationDto;
import ru.practicum.compliiation.dto.NewCompilationDto;
import ru.practicum.compliiation.dto.UpdateCompilationRequest;

public interface AdminCompilationService {
    CompilationDto create(NewCompilationDto newCompilationDto);

    void delete(Long compId);

    CompilationDto update(UpdateCompilationRequest request, Long compId);

}
