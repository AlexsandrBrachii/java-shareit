package ru.practicum.shareit.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.shareit.item.dto.CommentNewRequestDto;
import ru.practicum.shareit.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.shareit.utils.Utils;

import javax.validation.Valid;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping(value = "/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createComment(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @Valid @RequestBody CommentNewRequestDto commentNewDto
    ) {
        return itemClient.addComment(userId, itemId, commentNewDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @Valid @RequestBody ItemRequestDto itemDto
    ) {
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @RequestBody ItemRequestDto itemDto,
            @PathVariable long id
    ) {
        return itemClient.update(userId, id, itemDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @PathVariable Long id
    ) {
        return itemClient.findById(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size
    ) {
        Utils.checkPaging(from, size);
        return itemClient.findAllByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size
    ) {
        Utils.checkPaging(from, size);
        return itemClient.search(text, from, size);
    }
}

