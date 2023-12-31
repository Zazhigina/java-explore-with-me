package ru.practicum.compliiation.server.Impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.compliiation.dto.CompilationDto;
import ru.practicum.compliiation.dto.NewCompilationDto;
import ru.practicum.compliiation.dto.UpdateCompilationRequest;
import ru.practicum.compliiation.mapper.CompilationMapper;
import ru.practicum.compliiation.model.Compilation;
import ru.practicum.compliiation.repository.CompilationRepository;
import ru.practicum.compliiation.server.AdminCompilationService;
import ru.practicum.compliiation.server.PublicCompilationService;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCompilationServiceImpl implements AdminCompilationService {
    private final CompilationRepository compilationRepository;
    private final PublicCompilationService publicService;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        Set<Event> events = new HashSet<>();

        if (!Objects.isNull(newCompilationDto.getEvents())) {
            events = eventRepository.getByIdIn(newCompilationDto.getEvents());
        }

        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto, events);

        Compilation savedCompilation = compilationRepository.save(compilation);

        return CompilationMapper.toCompilationDto(savedCompilation, events.stream()
                .map(e -> EventMapper.toEventShortDto(e,
                        CategoryMapper.toCategoryDto(e.getCategory()),
                        UserMapper.toUserShortDto(e.getInitiator())))
                .collect(Collectors.toList()));
    }

    @Override
    public void delete(Long compId) {
        getCompilation(compId);
        publicService.getById(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto update(UpdateCompilationRequest request, Long compId) {
        Compilation compilation = getCompilation(compId);
        Set<Event> events = new HashSet<>();

        if (!Objects.isNull(request.getEvents())) {
            events = eventRepository.getByIdIn(request.getEvents());
        }
        compilation.setEvents(events);

        if (!Objects.isNull(request.getTitle())) {
            compilation.setTitle(request.getTitle());
        }

        if (!Objects.isNull(request.getPinned())) {
            compilation.setPinned(request.getPinned());
        }

        Compilation savedCompilation = compilationRepository.save(compilation);

        return CompilationMapper.toCompilationDto(savedCompilation, events.stream()
                .map(e -> EventMapper.toEventShortDto(e,
                        CategoryMapper.toCategoryDto(e.getCategory()),
                        UserMapper.toUserShortDto(e.getInitiator())))
                .collect(Collectors.toList()));
    }

    private Compilation getCompilation(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Compilation with id=%d was not found", compId),
                        Compilation.class,
                        LocalDateTime.now())
                );
    }
}
