package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.validated.Create;
import ru.practicum.shareit.validated.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private long id;

    @NotBlank(groups = {Create.class}, message = "Имя обязательное поле")
    private String name;

    @NotBlank(groups = {Create.class}, message = "Email обязательное поле")
    @Email(groups = {Create.class, Update.class}, message = "Не верный формат email")
    private String email;
}