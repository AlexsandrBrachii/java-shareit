package ru.practicum.shareit.user;


import java.util.Collection;

public interface UserService {

    UserDto getUserById(Long id);

    Collection<UserDto> getAllUsers();

    UserDto createUser(UserDto user);

    UserDto updateUser(Long id, UserDto userDto);

    void deleteUser(Long userId);
}
