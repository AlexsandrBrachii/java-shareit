package ru.practicum.shareit.user.storage;


import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;


import java.util.Collection;
import java.util.HashMap;

@Component
public class UserStorageInMemory {

    private static int idCounter = 0;
    HashMap<Integer, User> users = new HashMap<>();

    public User getUser(int userId) {
        return users.get(userId);
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public User createUser(User user) {
        validateUser(0, user.getEmail());
        user.setId(++idCounter);
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(int id, User user) {
        User userResult = null;
        if (user.getName() == null) {
            validateUser(id, user.getEmail());
            User userFromMemory = users.get(id);
            userFromMemory.setEmail(user.getEmail());
            userResult = userFromMemory;
            users.put(id, userFromMemory);
        } else if (user.getEmail() == null) {
            User userFromMemory = users.get(id);
            userFromMemory.setName(user.getName());
            userResult = userFromMemory;
            users.put(id, userFromMemory);
        } else {
            validateUser(id, user.getEmail());
            user.setId(id);
            users.put(id, user);
            userResult = user;
        }
        return userResult;
    }

    public void deleteUser(int userId) {
        users.remove(userId);
    }

    public void validateUser(int id, String email) {
        if (email == null || !email.contains("@")) {
            throw new ValidationException("Не корректный Email");
        }
        for (User existingUser : users.values()) {
            if (existingUser.getEmail().equals(email) && existingUser.getId() != id) {
                throw new RuntimeException("Email уже существует");
            }
        }
    }
}
