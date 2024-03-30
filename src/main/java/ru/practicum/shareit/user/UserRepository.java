package ru.practicum.shareit.user;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    default Optional<User> findByIdOrThrow(long id) {
        return findById(id).map(Optional::of).orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
    }

    default User searchByIdOrThrow(long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
    }
}