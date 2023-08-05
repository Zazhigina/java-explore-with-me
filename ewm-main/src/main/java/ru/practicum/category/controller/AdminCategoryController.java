package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.server.AdminCategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Validated
@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@RequestBody @Validated NewCategoryDto dto) {
        log.info("POST запрос на добавление категории.");
        return adminCategoryService.create(dto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long catId) {
        log.info("DELETE запрос на удаление категории по его id");
        adminCategoryService.delete(catId);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto update(@PathVariable @Min(0) long catId,
                              @Valid @RequestBody NewCategoryDto categoryDto) {
        log.info("PATCH запрос на обновление категории");
        return adminCategoryService.update(catId, categoryDto);
    }
}
