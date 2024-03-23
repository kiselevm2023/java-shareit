package ru.practicum.shareit.user;


import java.util.Optional;
import ru.practicum.shareit.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);

    default User findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
    }
}