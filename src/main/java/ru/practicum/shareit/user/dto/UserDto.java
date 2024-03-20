package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@RequiredArgsConstructor
public class UserDto {

    private long id;

    @NotBlank(groups = {Check.OnCreate.class}, message = "Поле name пустое")
    private String name;
    @NotEmpty(groups = {Check.OnCreate.class}, message = "The field email is empty")
    @Email(groups = {Check.OnCreate.class, Check.OnUpdate.class}, message = "The field email incorrect")
    private String email;

}