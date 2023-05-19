package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto objectToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail()).build();
    }

    public User dtoToObject(UserDto userDto) {
        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail()).build();
    }
}
