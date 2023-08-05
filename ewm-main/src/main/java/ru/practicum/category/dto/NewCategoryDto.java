package ru.practicum.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewCategoryDto {
    @NotBlank(message = "name cannot be empty or null.")
    @Size(min = 1, max = 50, message = "name cannot < 0 and > 50 symbols.")
    private String name;
}
