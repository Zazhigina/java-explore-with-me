package ru.practicum.compliiation.server.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compliiation.model.Compilation;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.compliiation.dto.CompilationDto;
import ru.practicum.compliiation.repository.CompilationRepository;
import ru.practicum.compliiation.server.PublicCompilationService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.category.mapper.CategoryMapper.toCategoryDto;
import static ru.practicum.compliiation.mapper.CompilationMapper.toCompilationDto;
import static ru.practicum.event.mapper.EventMapper.toEventShortDto;
import static ru.practicum.user.mapper.UserMapper.toUserShortDto;

@Service
@RequiredArgsConstructor
public class PublicCompilationServiceImpl implements PublicCompilationService {
    private final CompilationRepository compilationRepository;

    @Transactional(readOnly = true)
    @Override
    public List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);

        List<Compilation> compilations = compilationRepository.findAllByPinned(pinned, pageable);
        if (compilations.isEmpty()) return Collections.emptyList();
        return compilations.stream()
                .map(c -> toCompilationDto(c, c.getEvents().stream()
                        .map(e -> toEventShortDto(e, toCategoryDto(e.getCategory()), toUserShortDto(e.getInitiator())))
                        .collect(Collectors.toList()))).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getById(Long compId) {

        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> new EntityNotFoundException(String.format("Compilation with id=%d was not found", compId),
                Compilation.class,
                LocalDateTime.now()));

        return toCompilationDto(compilation, compilation.getEvents().stream()
                .map(e -> toEventShortDto(e,
                        toCategoryDto(e.getCategory()),
                        toUserShortDto(e.getInitiator())))
                .collect(Collectors.toList()));
    }
}
