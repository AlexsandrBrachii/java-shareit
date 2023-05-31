package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto getUserById(Long idUser) {
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь c id=%d не найден", idUser)));
        log.info("Возращен пользователь с id={}.", user.getId());
        return UserMapper.userToDto(user);
    }

    @Override
    @Transactional
    public Collection<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> usersDto = new ArrayList<>();
        for (User user : users) {
            usersDto.add(UserMapper.userToDto(user));
        }
        log.info("Возвращено {} пользователей.", users.size());
        return usersDto;
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = userRepository.save(UserMapper.dtoToUser(userDto));
        log.info("Сохранён пользователь {}", user.getId());
        return UserMapper.userToDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        if (userDto.getEmail() != null && userRepository.canNotUpdate(userDto.getId(), userDto.getEmail())) {
            throw new RuntimeException("Пользователь с таким email существует.");
        }

        User updatedUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь c id=%d не найден", userDto.getId())));

        if (userDto.getEmail() != null) {
            log.info("Обновляется email пользователя c id={}.", updatedUser.getId());
            updatedUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            log.info("Обновляется имя пользователя c id={}.", updatedUser.getId());
            updatedUser.setName(userDto.getName());
        }
        return UserMapper.userToDto(userRepository.save(updatedUser));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.info("Удаление пользователя с id={}.", userId);
        userRepository.deleteById(userId);
    }
}
