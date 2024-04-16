package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    default Optional<User> findByIdOrThrow(long id) {
        return findById(id).map(Optional::of).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    default User searchByIdOrThrow(long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }
}