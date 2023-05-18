package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping(value = "/{id}")
    private User getUser(@PathVariable int id) {
        return userService.getUser(id);
    }

    @GetMapping
    private Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    private User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PatchMapping(value = "/{id}")
    private User updateUser(@PathVariable int id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
    }

}
