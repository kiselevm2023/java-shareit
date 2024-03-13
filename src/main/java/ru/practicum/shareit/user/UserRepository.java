package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {

    List<User> getUsers();

    User getUserById(Long userId);

    User createUser(User user);

    User updateUser(User updateUser, Long userId);

    void deleteUser(Long userId);
}
