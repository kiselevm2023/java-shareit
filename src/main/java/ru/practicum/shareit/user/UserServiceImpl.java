package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll()
                .stream().map(x -> UserMapper.userToDto(x)).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        validateFoundForUser(user, userId);
        return UserMapper.userToDto(user.get());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        return UserMapper.userToDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        validateFoundForUser(userOptional, userId);
        User updateUser = userOptional.get();
        String updateName = userDto.getName();
        if (updateName != null && !updateName.isBlank()) {
            updateUser.setName(updateName);
        }
        String updateEmail = userDto.getEmail();
        if (updateEmail != null && !updateEmail.isBlank()) {
            updateUser.setEmail(updateEmail);
        }
        return UserMapper.userToDto(userRepository.save(updateUser));
    }

    @Override
    public void deleteUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        validateFoundForUser(user, userId);
        userRepository.deleteById(userId);
    }

    private void validateFoundForUser(Optional<User> user, long userId) {
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
    }
}