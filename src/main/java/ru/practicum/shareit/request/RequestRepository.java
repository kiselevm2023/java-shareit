package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query(value = "SELECT r FROM Request AS r WHERE r.userId = ?1 ORDER BY r.created DESC")
    List<Request> findForUser(long userId);

    @Query(value = "SELECT r FROM Request AS r WHERE r.userId <> ?1")
    List<Request> findAllExceptOwner(Pageable pageable, long userId);

}