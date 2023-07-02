package ru.practicum.shareit.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.shareit.request.dto.ItemRequestNewDto;
import ru.practicum.shareit.shareit.utils.Utils;

import javax.validation.Valid;

@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestClient requestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody ItemRequestNewDto requestDto
    ) {
        return requestClient.saveItemRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByRequestor(
            @RequestHeader("X-Sharer-User-Id") long userId
    ) {
        return requestClient.findAllByRequestor(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size
    ) {
        Utils.checkPaging(from, size);
        return requestClient.findItemRequests(userId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemRequest(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long id
    ) {
        return requestClient.getItemRequest(userId, id);
    }
}

