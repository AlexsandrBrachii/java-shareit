package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Возвращаем комментарии для вещи
    List<Comment> findCommentsByItemIdOrderByCreatedAsc(Long itemId);
}
