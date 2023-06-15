package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class UserIntegrationTest {

    private final EntityManager em;
    private final UserRepository userRepository;
    private final UserService userService;

    private User userA;
    private User userB;

    @BeforeEach
    void setUp() {
        init();
    }

    @Test
    void createUser_Normal() {
        UserDto userDto = UserDto.builder().name("owner").email("owner@example.com").build();

        UserDto saved = userService.createUser(userDto);

        Assertions.assertThat(saved).isNotNull()
                .hasFieldOrPropertyWithValue("name", "owner")
                .hasFieldOrPropertyWithValue("email", "owner@example.com")
                .hasFieldOrProperty("id")
                .hasNoNullFieldsOrProperties();
    }

    @Test
    void getUserById_Normal() {
        Long userId = userA.getId();

        UserDto returned = userService.getUserById(userId);

        Assertions.assertThat(returned).isNotNull()
                .hasFieldOrPropertyWithValue("name", "user")
                .hasFieldOrPropertyWithValue("email", "user@example.com")
                .hasFieldOrPropertyWithValue("id", userId)
                .hasNoNullFieldsOrProperties();
    }

    @Test
    void deleteUser_Normal() {
        Long userId = userA.getId();

        long beforeDelete = userRepository.count();
        Assertions.assertThat(beforeDelete).isEqualTo(2);

        userService.deleteUser(userId);

        long afterDelete = userRepository.count();
        Assertions.assertThat(afterDelete).isEqualTo(1);

        Optional<User> user = userRepository.findById(userId);
        Assertions.assertThat(user)
                .isNotPresent();
    }

    @Test
    void getAllUsers_Normal() {
        List<UserDto> list = (List<UserDto>) userService.getAllUsers();

        Assertions.assertThat(list)
                .isNotEmpty()
                .hasSize(2)
                .usingRecursiveComparison()
                .comparingOnlyFields("id", "name", "email")
                .isEqualTo(List.of(userA, userB));
    }

    private void init() {
        userA = User.builder().name("user").email("user@example.com").build();
        em.persist(userA);
        userB = User.builder().name("admin").email("admin@example.com").build();
        em.persist(userB);
    }
}
