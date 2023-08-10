package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {


    Comment save(Comment comment);

    @Query(value = " select c.id, c.text, c.item_id, c.author_id, c.created from Comments as c " +
            "where c.item_id = ?1 ", nativeQuery = true)
    List<Comment> findAllByItemId(long itemId);
}
