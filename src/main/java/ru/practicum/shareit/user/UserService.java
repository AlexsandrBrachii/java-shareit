package ru.practicum.shareit.user;


import java.util.Collection;

public interface UserService {

    UserDto getUserById(int id);

    Collection<UserDto> getAllUsers();

    UserDto createUser(UserDto user);

    UserDto updateUser(int id, UserDto userDto);

    void deleteUser(int userId);
}
