package ru.practicum.compliiation.server.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.compliiation.mapper.CompilationMapper;
import ru.practicum.compliiation.model.Compilation;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.compliiation.dto.CompilationDto;
import ru.practicum.compliiation.repository.CompilationRepository;
import ru.practicum.compliiation.server.PublicCompilationService;
import ru.practicum.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
                .map(c -> CompilationMapper.toCompilationDto(c, c.getEvents().stream()
                        .map(e -> EventMapper.toEventShortDto(e, CategoryMapper.toCategoryDto(e.getCategory()), UserMapper.toUserShortDto(e.getInitiator())))
                        .collect(Collectors.toList()))).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getById(Long compId) {

        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> new EntityNotFoundException(String.format("Compilation with id=%d was not found", compId),
                Compilation.class,
                LocalDateTime.now()));

        return CompilationMapper.toCompilationDto(compilation, compilation.getEvents().stream()
                .map(e -> EventMapper.toEventShortDto(e,
                        CategoryMapper.toCategoryDto(e.getCategory()),
                        UserMapper.toUserShortDto(e.getInitiator())))
                .collect(Collectors.toList()));
    }
}
