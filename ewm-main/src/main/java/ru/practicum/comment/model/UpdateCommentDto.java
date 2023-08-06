package ru.practicum.comment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCommentDto {
    @Size(max = 512, message = "text cannot be less more than 512.")
    private String text;
}