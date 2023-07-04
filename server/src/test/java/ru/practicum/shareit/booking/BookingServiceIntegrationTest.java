package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = { "db.name=test"})
@SpringBootTest
public class BookingServiceIntegrationTest {

    private final EntityManager em;
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;

    private final LocalDateTime now = LocalDateTime.now();

    User owner;
    User booker;
    Item item1;
    Item item2;

    // current approved
    Booking booking1;

    // future waiting
    Booking booking2;

    // past
    Booking booking3;

    // current canceled
    Booking booking4;

    @BeforeEach
    void setUp() {
        init();
    }

    @Test
    void createBooking_Normal() {
        Long userId = booker.getId();
        Long itemId = item1.getId();

        BookingNewDto newBooking = BookingNewDto.builder()
                .itemId(itemId)
                .start(now.plusDays(2)).end(now.plusDays(4))
                .build();

        BookingDto created = bookingService.createBooking(userId, newBooking);

        Booking retrievedBooking = bookingRepository.findById(created.getId()).orElse(null);
        Assertions.assertThat(retrievedBooking).isNotNull();
        Assertions.assertThat(retrievedBooking.getStatus()).isEqualTo(BookingStatus.WAITING);
        Assertions.assertThat(retrievedBooking.getBooker().getId()).isEqualTo(userId);
        Assertions.assertThat(retrievedBooking.getItem().getId()).isEqualTo(itemId);
    }

    @Test
    void approve_Normal() {
        //approve booking2;
        Long userId = owner.getId();
        Long bookingId = booking2.getId();

        Assertions.assertThat(bookingService.getBooking(booker.getId(), bookingId))
                .hasFieldOrPropertyWithValue("status", "WAITING");

        // approve
        BookingDto approvedBooking = bookingService.approve(userId, true, bookingId);

        Assertions.assertThat(approvedBooking).isNotNull()
                .hasFieldOrPropertyWithValue("status", "APPROVED");
    }

    @Test
    void getBooking_Normal() {
        Long userId = booker.getId();
        Long bookingId = booking3.getId();

        BookingDto finder = bookingService.getBooking(userId, bookingId);

        Assertions.assertThat(finder).isNotNull()
                .hasFieldOrPropertyWithValue("id", bookingId);
        Assertions.assertThat(finder.getBooker())
                .hasFieldOrPropertyWithValue("id", userId);
    }

    @Test
    void getAllBookings_All() {
        List<BookingDto> list1 = bookingService.getAllBookings(booker.getId(), "ALL", 0, 20);
        Assertions.assertThat(list1).isNotEmpty().hasSize(4);
    }

    @Test
    void getAllBookings_Past() {
        List<BookingDto> list2 = bookingService.getAllBookings(booker.getId(), "PAST", 0, 20);
        Assertions.assertThat(list2).isNotEmpty().hasSize(1);
    }

    @Test
    void getAllBookings_Future() {
        List<BookingDto> list3 = bookingService.getAllBookings(booker.getId(), "FUTURE", 0, 20);
        Assertions.assertThat(list3).isNotEmpty().hasSize(1);
    }

    @Test
    void getAllBookings_Current() {
        List<BookingDto> list4 = bookingService.getAllBookings(booker.getId(), "CURRENT", 0, 20);
        Assertions.assertThat(list4).isNotEmpty().hasSize(2);
    }

    @Test
    void getAllBookings_Rejected() {
        List<BookingDto> list5 = bookingService.getAllBookings(booker.getId(), "REJECTED", 0, 20);
        Assertions.assertThat(list5).isNotEmpty().hasSize(2);
    }

    @Test
    void getAllBookings_Waiting() {
        List<BookingDto> list6 = bookingService.getAllBookings(booker.getId(), "WAITING", 0, 20);
        Assertions.assertThat(list6).isNotEmpty().hasSize(1);
    }

    @Test
    void getAllBookingsForOwner_All() {
        List<BookingDto> list1 = bookingService.getAllBookingsForOwner(owner.getId(), "ALL", 0, 20);
        Assertions.assertThat(list1).isNotEmpty().hasSize(4);
    }

    @Test
    void getAllBookingsForOwner_Past() {
        List<BookingDto> list2 = bookingService.getAllBookingsForOwner(owner.getId(), "PAST", 0, 20);
        Assertions.assertThat(list2).isNotEmpty().hasSize(1);
    }

    @Test
    void getAllBookingsForOwner_Future() {
        List<BookingDto> list3 = bookingService.getAllBookingsForOwner(owner.getId(), "FUTURE", 0, 20);
        Assertions.assertThat(list3).isNotEmpty().hasSize(1);
    }

    @Test
    void getAllBookingsForOwner_Current() {
        List<BookingDto> list4 = bookingService.getAllBookingsForOwner(owner.getId(), "CURRENT", 0, 20);
        Assertions.assertThat(list4).isNotEmpty().hasSize(2);
    }

    @Test
    void getAllBookingsForOwner_Rejected() {
        List<BookingDto> list5 = bookingService.getAllBookingsForOwner(owner.getId(), "REJECTED", 0, 20);
        Assertions.assertThat(list5).isNotEmpty().hasSize(2);
    }

    @Test
    void getAllBookingsForOwner_Waiting() {
        List<BookingDto> list6 = bookingService.getAllBookingsForOwner(owner.getId(), "WAITING", 0, 20);
        Assertions.assertThat(list6).isNotEmpty().hasSize(1);
    }

    private void init() {
        owner = User.builder().name("owner").email("owner@example.com").build();
        em.persist(owner);
        booker = User.builder().name("booker").email("booker@example.com").build();
        em.persist(booker);
        item1 = Item.builder()
                .name("молоток").description("хороший молоток").available(true)
                .owner(owner).build();
        em.persist(item1);
        item2 = Item.builder()
                .name("дрель").description("мощная дрель").available(true)
                .owner(owner).build();
        em.persist(item2);
        // current approved
        booking1 = Booking.builder()
                .item(item1).booker(booker).status(BookingStatus.APPROVED)
                .start(now.minusDays(1)).end(now.plusDays(1))
                .build();
        em.persist(booking1);
        // future waiting
        booking2 = Booking.builder()
                .item(item1).booker(booker).status(BookingStatus.WAITING)
                .start(now.plusDays(1)).end(now.plusDays(2))
                .build();
        em.persist(booking2);
        // past
        booking3 = Booking.builder()
                .item(item2).booker(booker).status(BookingStatus.REJECTED)
                .start(now.minusDays(2)).end(now.minusDays(1))
                .build();
        em.persist(booking3);
        // current canceled
        booking4 = Booking.builder()
                .item(item2).booker(booker).status(BookingStatus.CANCELED)
                .start(now.minusDays(1)).end(now.plusDays(1))
                .build();
        em.persist(booking4);
    }
}
