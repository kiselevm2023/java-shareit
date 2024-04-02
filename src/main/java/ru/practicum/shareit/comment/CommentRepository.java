package ru.practicum.shareit.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByItem_Id(long itemId);

    List<Comment> findByItemIn(List<Item> items, Sort sort);

    List<Comment> findAllByItemId(long itemId);

    List<Comment> findAllByItemIdOrderByIdDesc(long itemId);
}