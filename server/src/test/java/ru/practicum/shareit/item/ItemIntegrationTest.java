package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = { "db.name=test"})
@SpringBootTest
public class ItemIntegrationTest {

    private final EntityManager em;
    private final ItemService itemService;

    private final LocalDateTime now = LocalDateTime.now();

    private User owner;
    private User booker;
    private User booker2;
    private Item item;
    private Item item2;
    private Booking booking;
    private Booking booking2;
    private Comment comment2;

    @BeforeEach
    void setUp() {
        init();
    }

    @Test
    void createItem_Normal() {
        Long userId = owner.getId();
        ItemDto newItem = ItemDto.builder()
                .name("дрель").description("дрель электрическая")
                .available(true)
                .build();

        ItemDto saved = itemService.createItem(userId, newItem);

        Assertions.assertThat(saved).isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(newItem);
    }

    @Test
    void updateItem_Normal() {
        Long userId = owner.getId();
        ItemDto updater = ItemMapper.itemToDto(item);
        updater.setName("новый молоток");
        updater.setDescription("новый стальной молоток");
        updater.setAvailable(false);

        ItemDto updated = itemService.updateItem(userId, updater);

        Assertions.assertThat(updated).isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updater);
    }

    /*@Test
    void getAllItems_Normal() {
        Long ownerId = owner.getId();
        Long bookerId = booker.getId();

        List<ItemBookingDto> returnedList = (List<ItemBookingDto>) itemService.getAllItems(ownerId);
        Assertions.assertThat(returnedList)
                .isNotEmpty()
                .hasSize(2);
        Assertions.assertThat(returnedList.get(0).getName()).isEqualTo(item.getName());
        Assertions.assertThat(returnedList.get(1).getName()).isEqualTo(item2.getName());


        List<ItemBookingDto> returnedList2 = (List<ItemBookingDto>) itemService.getAllItems(bookerId);
        Assertions.assertThat(returnedList2).isEmpty();
    }

    @Test
    void findItems_Normal() {
        String search = "стальной";

        List<ItemDto> list = itemService.findItems(search);

        Assertions.assertThat(list).isNotEmpty().hasSize(2);
        Assertions.assertThat(list.get(0).getName()).isEqualTo(item.getName());
        Assertions.assertThat(list.get(1).getName()).isEqualTo(item2.getName());
    }

    @Test
    void findItems_EmptySearchText_Normal() {
        String search = "";

        List<ItemDto> list = itemService.findItems(search);

        Assertions.assertThat(list).isEmpty();
    }

    @Test
    void addComment_Normal() {
        CommentNewDto commentNewDto = CommentNewDto.builder()
                .text("отличный молоток")
                .build();

        CommentDto addedComment = itemService.addComment(booker.getId(), item.getId(), commentNewDto);

        Assertions.assertThat(addedComment).isNotNull()
                .hasFieldOrPropertyWithValue("text", commentNewDto.getText())
                .hasFieldOrPropertyWithValue("authorName", booker.getName());

    }*/

    private void init() {
        owner = User.builder()
                .name("owner").email("owner@example.com")
                .build();
        em.persist(owner);
        booker = User.builder()
                .name("booker").email("booker@example.com")
                .build();
        em.persist(booker);
        booker2 = User.builder()
                .name("booker2").email("booker2@example.com")
                .build();
        em.persist(booker2);
        // Вещь пользователя owner
        item = Item.builder()
                .name("молоток").description("стальной молоток").available(true)
                .owner(owner)
                .build();
        em.persist(item);
        item2 = Item.builder()
                .name("гвоздодер").description("стальной гвоздодер").available(true)
                .owner(owner)
                .build();
        em.persist(item2);
        // Бронирование пользователем booker
        booking = Booking.builder()
                .item(item).booker(booker).status(BookingStatus.APPROVED)
                .start(now.minusDays(5))
                .end(now.minusDays(4))
                .build();
        em.persist(booking);
        // Бронирование пользователем booker2
        booking2 = Booking.builder()
                .item(item).booker(booker2).status(BookingStatus.APPROVED)
                .start(now.minusDays(3))
                .end(now.minusDays(2))
                .build();
        em.persist(booking2);
        // Комментарий пользователя booker2
        comment2 = Comment.builder()
                .text("Молоток удобный")
                .item(item)
                .created(now.minusDays(3))
                .author(booker2)
                .build();
        em.persist(comment2);
    }
}
