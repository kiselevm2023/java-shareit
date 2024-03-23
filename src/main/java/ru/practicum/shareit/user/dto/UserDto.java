package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@RequiredArgsConstructor
public class UserDto {

    private long id;

    @NotBlank(groups = {Check.OnCreate.class}, message = "Поле name пустое")
    @Size(groups = {Check.OnCreate.class, Check.OnUpdate.class}, max = 255, message = "The name length must be no more than {max} characters")
    private String name;
    @NotEmpty(groups = {Check.OnCreate.class}, message = "The field email is empty")
    @Email(groups = {Check.OnCreate.class, Check.OnUpdate.class}, message = "The field email incorrect")
    @Size(groups = {Check.OnCreate.class, Check.OnUpdate.class}, max = 512, message = "The email length must be no more than {max} characters")
    private String email;
}