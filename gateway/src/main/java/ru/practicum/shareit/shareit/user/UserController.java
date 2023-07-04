package ru.practicum.shareit.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.shareit.user.dto.UserRequestDto;

import javax.validation.Valid;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestBody @Valid UserRequestDto requestDto) {
        return userClient.create(requestDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(
            @PathVariable long id,
            @RequestBody UserRequestDto requestDto
    ) {
        return userClient.update(id, requestDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(
            @PathVariable long id
    ) {
        return userClient.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable long id
    ) {
        userClient.delete(id);
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return userClient.findAll();
    }
}

