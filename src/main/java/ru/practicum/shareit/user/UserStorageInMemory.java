package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;

@Component
public class UserStorageInMemory {

    private static int idCounter = 0;
    private HashMap<Integer, User> users = new HashMap<>();

    public User getUser(int userId) {
        return users.get(userId);
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public User createUser(User user) {
        user.setId(++idCounter);
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(int id, User user) {
        User userResult = null;
        if (user.getEmail() != null) {
            User userFromMemory = users.get(id);
            userFromMemory.setEmail(user.getEmail());
            userResult = userFromMemory;
            users.put(id, userFromMemory);
        }
        if (user.getName() != null) {
            User userFromMemory = users.get(id);
            userFromMemory.setName(user.getName());
            userResult = userFromMemory;
            users.put(id, userFromMemory);
        }
        if (user.getEmail() != null && user.getName() != null) {
            user.setId(id);
            users.put(id, user);
            userResult = user;
        }
        return userResult;
    }

    public void deleteUser(int userId) {
        users.remove(userId);
    }

    public Collection<User> getUsers() {
        return users.values();
    }
}
