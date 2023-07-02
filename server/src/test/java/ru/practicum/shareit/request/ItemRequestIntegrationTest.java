package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = { "db.name=test"})
@SpringBootTest
public class ItemRequestIntegrationTest {

    private final EntityManager em;
    private final ItemRequestService itemRequestService;

    private User owner;
    private User user;
    private ItemRequest request;
    private ItemRequest request2;

    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        init();
    }

    @Test
    void createItemRequest_Normal() {

        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("стул")
                .build();

        ItemRequestDto saved = itemRequestService.createItemRequest(user.getId(), requestDto);


        String query = "select ir from ItemRequest as ir where ir.id=:id";

        ItemRequest result = em.createQuery(query, ItemRequest.class)
                .setParameter("id", saved.getId())
                .getSingleResult();

        assertThat(result, notNullValue());
        assertThat(result.getRequestor().getName(), equalTo(user.getName()));
        assertThat(result.getRequestor().getEmail(), equalTo(user.getEmail()));
        assertThat(result.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(result.getId(), equalTo(saved.getId()));
    }

    @Test
    void finsAllByRequestor_Normal() {
        Long requestorId = user.getId();

        List<ItemRequestDto> list = itemRequestService.findAllByRequestor(requestorId);

        Assertions.assertThat(list).isNotEmpty().hasSize(2);
    }

    @Test
    void findItemRequests_Normal_Return1() {
        Long userId = owner.getId();

        List<ItemRequestDto> list = itemRequestService.findItemRequests(userId, 0, 20);

        Assertions.assertThat(list).isNotEmpty().hasSize(2);
    }

    @Test
    void findItemRequests_Normal_Return0() {
        Long userId = user.getId();

        List<ItemRequestDto> list = itemRequestService.findItemRequests(userId, 0, 20);

        Assertions.assertThat(list).isEmpty();
    }

    @Test
    void getItemRequest_Normal() {

        ItemRequestDto itemRequestDto = itemRequestService.getItemRequest(user.getId(), request.getId());


        String query = "select ir from ItemRequest as ir where ir.id=:id";
        ItemRequest result = em.createQuery(query, ItemRequest.class)
                .setParameter("id", itemRequestDto.getId())
                .getSingleResult();

        //then
        assertThat(itemRequestDto.getCreated(), is(now.minusHours(3)));
        assertThat(itemRequestDto.getDescription(), is(request.getDescription()));
        assertThat(result.getRequestor(), is(user));
        assertThat(result.getItems(), nullValue());
    }

    private void init() {
        user = User.builder()
                .name("user")
                .email("user@example.com")
                .build();
        em.persist(user);
        owner = User.builder()
                .name("owner")
                .email("owner@example.com")
                .build();
        em.persist(owner);
        request = ItemRequest.builder()
                .description("стремянка")
                .created(now.minusHours(3))
                .requestor(user)
                .build();
        em.persist(request);
        request2 = ItemRequest.builder()
                .description("стул")
                .created(now.minusHours(6))
                .requestor(user)
                .build();
        em.persist(request2);
    }
}
