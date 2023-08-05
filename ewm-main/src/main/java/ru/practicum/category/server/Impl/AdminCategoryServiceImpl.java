package ru.practicum.category.server.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.category.server.AdminCategoryService;
import ru.practicum.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto create(NewCategoryDto categoryDto) {
        Category category = CategoryMapper.toCategory(categoryDto);
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void delete(Long catId) {
        getCategory(catId);
        Optional<Event> eventList = eventRepository.findAllByCategoryId(catId);
        if (!eventList.isEmpty()) {
            throw new ValidationException(
                    "The category is not empty",
                    LocalDateTime.now());
        }

        categoryRepository.deleteById(catId);
    }

    @Override
    @Transactional
    public CategoryDto update(Long catId, NewCategoryDto categoryDto) {
        Category category = getCategory(catId);
        category.setName(categoryDto.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    private Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Category wit id=%d was not found", id),
                        Category.class,
                        LocalDateTime.now())
                );
    }
}
