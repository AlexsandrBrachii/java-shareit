package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorageInMemory;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserStorageInMemory userStorage;

    @Override
    public User getUser(int id) {
        return userStorage.getUser(id);
    }

    @Override
    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    @Override
    public User updateUser(int id, User user) {
        return userStorage.updateUser(id, user);
    }

    @Override
    public void deleteUser(int userId) {
        userStorage.deleteUser(userId);
    }
}
