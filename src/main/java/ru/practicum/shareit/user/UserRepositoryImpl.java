package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.AlreadyExsist;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserRepositoryImpl implements UserRepository {

    private Map<Long, User> users = new HashMap<>();
    private final Map<String, User> userEmailRepository = new HashMap<>();
    Long userId = 1L;

    @Override
    public List<User> getUsers() {
        return users.values().stream().collect(Collectors.toList());
    }

    @Override
    public User getUserById(Long userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            throw new NotFoundException("User does not exist");
        }
    }

    @Override
    public User createUser(User user) {
        if (userEmailRepository.containsKey(user.getEmail())) {
            throw new AlreadyExsist("User with this email already exists");
        } else {
            user.setId(userId);
            users.put(userId, user);
            userId++;
            userEmailRepository.put(user.getEmail(), user);
        }
        return user;
    }

    @Override
    public User updateUser(User user, Long userId) {
        validateFoundForUser(userId);
        validateForExistEmailWithOtherOwner(userId, user);
        User updateUser = users.get(userId);
        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userEmailRepository.remove(updateUser.getEmail());
            updateUser.setEmail(user.getEmail());
            userEmailRepository.put(updateUser.getEmail(), updateUser);
        }
        users.put(userId, updateUser);
        return updateUser;
    }

    @Override
    public void deleteUser(Long userId) {
        if (users.containsKey(userId)) {
            userEmailRepository.remove(users.get(userId).getEmail());
            users.remove(userId);
        } else {
            throw new NotFoundException("User deletion error: user is not founded");
        }
    }

    private void validateFoundForUser(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("User is not founded");
        }
    }

    private void validateForExistEmailWithOtherOwner(long userId, User user) {
        if (userEmailRepository.containsKey(user.getEmail())) {
            if (userEmailRepository.get(user.getEmail()).getId() != userId)
                throw new AlreadyExsist("User with this email already exists");
        }
    }
}