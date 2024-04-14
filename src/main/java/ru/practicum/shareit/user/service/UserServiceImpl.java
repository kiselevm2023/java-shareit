package ru.practicum.shareit.user.service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper userMapper;

    public List<UserDto> getListUsers() {
        List<User> users = repository.findAll();

        return userMapper.toUserDto(users);
    }

    public UserDto createUser(UserDto userDto) {

        return userMapper.toUserDto(repository.save(userMapper.toUser(userDto)));
    }

    public UserDto updateUser(Long id, UserDto user) {
        Optional<User> userOptional = repository.findByIdOrThrow(id);
        User updateUser = userOptional.get();
        String updateName = user.getName();
        if (updateName != null && !updateName.isBlank()) {
            updateUser.setName(updateName);
        }
        String updateEmail = user.getEmail();
        if (updateEmail != null && !updateEmail.isBlank()) {
            updateUser.setEmail(updateEmail);
        }
        return userMapper.toUserDto(repository.save(updateUser));
    }

    public UserDto getUserById(Long id) {
        User user = repository.searchByIdOrThrow(id);

        return userMapper.toUserDto(user);
    }

    public void deleteUser(Long id) {
        repository.searchByIdOrThrow(id);

        repository.deleteById(id);
    }
}