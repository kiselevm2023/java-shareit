package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
public class UserDto {

    private long id;
    @NotBlank(message = "Поле name пустое")
    private String name;
    @NotBlank(message = "Поле email пустое")
    @Email(message = "Поле email некорректно")
    private String email;
}