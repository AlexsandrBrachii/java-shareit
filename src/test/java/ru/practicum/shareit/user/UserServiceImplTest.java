package ru.practicum.shareit.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .name("user")
                .email("user@example.com")
                .build();

        user = User.builder()
                .id(1L)
                .name("user")
                .email("user@example.com")
                .build();
    }

    @Test
    void createUser_Normal_ReturnUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto added = userService.createUser(userDto);

        Assertions.assertThat(added)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user);

        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void createUser_WhenEmailExists_Exception() {
        when(userRepository.save(any(User.class)))
                .thenThrow(ValidationException.class);

        Throwable thrown = Assertions.catchException(() -> userService.createUser(userDto));

        Assertions.assertThat(thrown)
                .isInstanceOf(ValidationException.class);

        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUser_NameAndEmail_Normal() {
        final UserDto updatedData = UserDto.builder()
                .id(1L)
                .name("updatedName")
                .email("updatedName@example.com")
                .build();

        when(userRepository.canNotUpdate(1L, "updatedName@example.com")).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserDto updatedUser = userService.updateUser(1L, updatedData);

        Assertions.assertThat(updatedUser.getEmail()).isEqualTo("updatedName@example.com");
        Assertions.assertThat(updatedUser.getName()).isEqualTo("updatedName");

        Mockito.verify(userRepository, Mockito.times(1)).canNotUpdate(1L, "updatedName@example.com");
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUser_EmailIsExists_Exception() {
        final UserDto updatedData = UserDto.builder()
                .id(2L)
                .name("user2")
                .email("user@example.com")
                .build();
        when(userRepository.canNotUpdate(2L, "user@example.com"))
                .thenReturn(true);

        Throwable thrown = Assertions.catchException(() -> userService.updateUser(2L, updatedData));

        Assertions.assertThat(thrown)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Пользователь с таким email существует.");

        Mockito.verify(userRepository, Mockito.times(1)).canNotUpdate(2L, "user@example.com");
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUser_OnlyName_Normal() {
        UserDto updatedData = UserDto.builder()
                .id(1L)
                .name("updatedName")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserDto updatedUser = userService.updateUser(1L, updatedData);

        Assertions.assertThat(updatedUser.getEmail()).isEqualTo("user@example.com");
        Assertions.assertThat(updatedUser.getName()).isEqualTo("updatedName");

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUser_OnlyEmail_Normal() {
        UserDto updatedData = UserDto.builder()
                .id(1L)
                .email("updatedName@example.com")
                .build();

        when(userRepository.canNotUpdate(1L, "updatedName@example.com")).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserDto updatedUser = userService.updateUser(1L, updatedData);

        Assertions.assertThat(updatedUser.getEmail()).isEqualTo("updatedName@example.com");
        Assertions.assertThat(updatedUser.getName()).isEqualTo("user");

        Mockito.verify(userRepository, Mockito.times(1)).canNotUpdate(1L, "updatedName@example.com");
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUser_EmailIfDuplicate_Normal() {
        UserDto updatedData = UserDto.builder()
                .id(1L)
                .email("user@example.com")
                .build();

        when(userRepository.canNotUpdate(1L, "user@example.com")).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserDto returnedUser = userService.updateUser(1L, updatedData);

        Assertions.assertThat(returnedUser.getEmail()).isEqualTo("user@example.com");
        Assertions.assertThat(returnedUser.getName()).isEqualTo("user");

        Mockito.verify(userRepository, Mockito.times(1)).canNotUpdate(1L, "user@example.com");
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserById_Normal_ReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto actual = userService.getUserById(1L);

        Assertions.assertThat(actual)
                .isNotNull()
                .usingRecursiveComparison().isEqualTo(user);
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserById_WrongId_NotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Throwable thrown = Assertions.catchException(() -> userService.getUserById(999L));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessage(String.format("Пользователь c id=%d не найден", 999L));

        Mockito.verify(userRepository, Mockito.times(1)).findById(999L);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteUser_Normal() {
        Mockito.doNothing().when(userRepository).deleteById(anyLong());

        userService.deleteUser(anyLong());

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(anyLong());
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAllUsers_Normal_ReturnTwoUsers() {
        User user2 = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@example.com")
                .build();
        when(userRepository.findAll()).thenReturn(List.of(user, user2));

        List<UserDto> users = (List<UserDto>) userService.getAllUsers();

        Assertions.assertThat(users)
                .isNotNull()
                .hasSize(2)
                .contains(UserMapper.userToDto(user),
                        UserMapper.userToDto(user2));
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(userRepository);
    }
}
