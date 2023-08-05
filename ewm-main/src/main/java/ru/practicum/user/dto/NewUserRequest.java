package ru.practicum.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {
    @NotBlank
    @Email
    @Size(min = 6, max = 254)
    private String email;
    @NotBlank
    @Size(min = 2, max = 250)
    private String name;
}
