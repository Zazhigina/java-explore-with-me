package ru.practicum.compliiation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compliiation.dto.CompilationDto;
import ru.practicum.compliiation.dto.NewCompilationDto;
import ru.practicum.compliiation.dto.UpdateCompilationRequest;
import ru.practicum.compliiation.server.AdminCompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Min;


@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCompilationsController {
    private final AdminCompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@RequestBody @Valid NewCompilationDto dto) {
        log.info("POST запрос на добавление подборок событии.");
        return compilationService.create(dto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Min(0) long compId) {
        log.info("DELETE запрос на удаление подбороки событии по его id");
        compilationService.delete(compId);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto update(@Valid @RequestBody UpdateCompilationRequest request,
                                 @PathVariable long compId) {
        log.info("PATCH запрос на обновление информации о подборке");
        return compilationService.update(request, compId);
    }
}


