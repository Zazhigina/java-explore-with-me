package ru.practicum.compliiation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationDto {
    private List<Long> events;
    @Builder.Default
    private Boolean pinned = false;
    @NotBlank(message = "title cannot be empty or null.")
    @Size(min = 1, max = 50, message = "title cannot be less 1 or more than 50.")
    private String title;
}
