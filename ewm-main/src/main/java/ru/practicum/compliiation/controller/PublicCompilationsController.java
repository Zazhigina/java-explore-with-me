package ru.practicum.compliiation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compliiation.dto.CompilationDto;
import ru.practicum.compliiation.server.PublicCompilationService;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicCompilationsController {
    private final PublicCompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getAll(@RequestParam(value = "pinned", required = false) boolean pinned,
                                       @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
                                       @Positive @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        log.info("GET запрос на получение подборок событии.");
        return compilationService.getAll(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getById(@PathVariable @Min(0) long compId) {
        log.info("GET запрос на получение подбороки событии по его id");
        return compilationService.getById(compId);
    }
}
