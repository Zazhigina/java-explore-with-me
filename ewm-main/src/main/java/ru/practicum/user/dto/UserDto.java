package ru.practicum.user.dto;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class UserDto {
    private Long id;
    private String name;
    private String email;
}
