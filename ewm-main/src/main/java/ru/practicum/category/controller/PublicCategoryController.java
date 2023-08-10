package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.server.PublicCategoryService;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
@Slf4j
public class PublicCategoryController {
    private final PublicCategoryService publicCategoryService;

    @GetMapping
    public List<CategoryDto> get(@PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                 @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Get запрос на получение категории.");
        return publicCategoryService.findCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getById(@PathVariable @Min(0) long catId) {
        log.info("Get запрос на получение категории по id.");
        return publicCategoryService.findById(catId);
    }
}
