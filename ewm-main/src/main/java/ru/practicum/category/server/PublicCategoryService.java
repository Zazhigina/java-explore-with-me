package ru.practicum.category.server;

import ru.practicum.category.dto.CategoryDto;

import java.util.List;

public interface PublicCategoryService {

    List<CategoryDto> findCategories(Integer from, Integer size);

    CategoryDto findById(Long id);
}
