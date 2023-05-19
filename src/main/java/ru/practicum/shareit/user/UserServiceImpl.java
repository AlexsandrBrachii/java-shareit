package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserStorageInMemory userStorage;
    private final UserMapper userMapper;

    @Override
    public UserDto getUserById(int idUser) {
        User user = userStorage.getUser(idUser);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователя с id=%s не найдено", idUser));
        }
        return userMapper.objectToDto(user);
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        Collection<User> users = userStorage.getAllUsers();
        List<UserDto> usersDto = new ArrayList<>();
        for (User user : users) {
            usersDto.add(userMapper.objectToDto(user));
        }
        return usersDto;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userStorage.createUser(userMapper.dtoToObject(userDto));
        return userMapper.objectToDto(user);
    }

    @Override
    public UserDto updateUser(int id, UserDto userDto) {
        User user = userStorage.updateUser(id, userMapper.dtoToObject(userDto));
        return userMapper.objectToDto(user);
    }

    @Override
    public void deleteUser(int userId) {
        userStorage.deleteUser(userId);
    }
}
