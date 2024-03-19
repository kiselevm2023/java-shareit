package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.AlreadyExsist;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserRepositoryImpl {

    private Map<Long, User> users = new HashMap<>();//айди юзера и сам юзер
    private final Map<String, User> userEmailRepository = new HashMap<>();
    Long userId = 1L;


    public List<User> getUsers() {
        return users.values().stream().collect(Collectors.toList());
    }


    public User getUserById(Long userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            throw new NotFoundException("пользователя не существует");
        }
    }


    public User createUser(User user) {
        if (userEmailRepository.containsKey(user.getEmail())) {
            throw new AlreadyExsist("Пользователь c такой почтой уже существует");
        } else {
            user.setId(userId);
            users.put(userId, user);
            userId++;
            userEmailRepository.put(user.getEmail(), user);
        }
        return user;
    }


    public User updateUser(User user, Long userId) {
        validateFoundForUser(userId);
        user.setId(userId);
        if (user.getEmail() != null && user.getName() != null) {
            userEmailRepository.remove(users.get(userId).getEmail());
            users.get(userId).setEmail(user.getEmail());
            users.get(userId).setName(user.getName());
            userEmailRepository.put(users.get(userId).getEmail(), users.get(userId));
        } else if (user.getEmail() != null) {
            validateForExistEmailWithOtherOwner(userId, user);
            userEmailRepository.remove(users.get(userId).getEmail());
            users.get(userId).setEmail(user.getEmail());
            userEmailRepository.put(users.get(userId).getEmail(), users.get(userId));
            user.setName(users.get(userId).getName());
        } else {
            users.get(userId).setName(user.getName());
            userEmailRepository.put(users.get(userId).getEmail(), users.get(userId));
            user.setEmail(users.get(userId).getEmail());
        }
        return user;
    }


    public void deleteUser(Long userId) {
        if (users.containsKey(userId)) {
            userEmailRepository.remove(users.get(userId).getEmail());
            users.remove(userId);
        } else {
            throw new NotFoundException("Ошибка удаления пользователя: пользователь не найден");
        }
    }

    private void validateFoundForUser(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private void validateForExistEmailWithOtherOwner(long userId, User user) {
        if (userEmailRepository.containsKey(user.getEmail())) {
            if (userEmailRepository.get(user.getEmail()).getId() != userId)
                throw new AlreadyExsist("Пользователь с таким email уже существует");
        }
    }
}