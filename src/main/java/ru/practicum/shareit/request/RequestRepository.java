package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    //@Query(value = "SELECT r FROM Request AS r WHERE r.userId = ?1 ORDER BY r.created DESC")
    //List<Request> findForUser(long userId);

    //List<Item> findByRequestRequestId(Long itemRequestId, Sort sort);
    //List<Item> findByItemRequestId(Long itemRequestId, Sort sort);



    //@Query(value = "SELECT r FROM Request AS r WHERE r.user.Id) <> ?1")
    //List<Request> findAllExceptOwner(Pageable pageable, long userId);


    List<Request> findAllByAuthorId(Long userId, Sort sort);

    Page<Request> findAllByAuthorIdNot(Long userId, Pageable pageable);

}