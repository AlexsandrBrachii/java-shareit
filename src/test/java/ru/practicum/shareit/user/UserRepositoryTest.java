package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = { "db.name=test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserRepositoryTest {

    private final UserRepository userRepository;
    private final TestEntityManager tem;

    @Test
    void test_canNotUpdate() {
        User user1 = User.builder()
                .name("user")
                .email("user@example.com")
                .build();
        tem.persist(user1);
        User user2 = User.builder()
                .name("tester")
                .email("tester@example.com")
                .build();
        tem.persist(user2);

        boolean resultFalse = userRepository.canNotUpdate(user1.getId(),"tester@example.com");
        Assertions.assertThat(resultFalse).isTrue();

        boolean resultTrue = userRepository.canNotUpdate(user1.getId(),"user-user@example.com");
        Assertions.assertThat(resultTrue).isFalse();
    }
}
