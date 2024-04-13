package ru.practicum.shareit.user.service;

import java.util.List;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    List<UserDto> getListUsers();

    UserDto createUser(UserDto userDto);

    UserDto updateUser(Long id, UserDto user);

    UserDto getUserById(Long id);

    void deleteUser(Long id);
}