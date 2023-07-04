package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@DataJpaTest
@TestPropertySource(properties = { "db.name=test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingRepositoryTest {

    private final TestEntityManager tem;
    private final BookingRepository bookingRepository;

    private final LocalDateTime now = LocalDateTime.now();

    @Test
    void test_findBookingForComment() {
        User owner = User.builder().name("owner").email("owner@example.com").build();
        tem.persist(owner);
        Item item = Item.builder()
                .name("молоток").description("стальной молоток").available(true)
                .owner(owner).build();
        tem.persist(item);
        User booker = User.builder().name("booker").email("booker@example.com").build();
        tem.persist(booker);
        Booking booking = Booking.builder()
                .item(item).booker(booker)
                .start(now.minusDays(5)).end(now.minusDays(4))
                .status(BookingStatus.APPROVED)
                .build();
        tem.persist(booking);

        PageRequest page = PageRequest.of(0, 1);

        Page<Booking> finder = bookingRepository.findBookingForComment(booker.getId(), item.getId(), now, BookingStatus.APPROVED,
                page);
        Assertions.assertThat(finder.get().findFirst().orElse(null)).isNotNull().isEqualTo(booking);

        Page<Booking> finder2 = bookingRepository.findBookingForComment(999L, item.getId(), now, BookingStatus.APPROVED,
                page);
        Assertions.assertThat(finder2.get().findFirst().orElse(null)).isNull();
    }

    @Test
    void test_findBookingByOwnerOrBooker() {
        User owner = User.builder().name("owner").email("owner@example.com").build();
        tem.persist(owner);
        Item item = Item.builder()
                .name("молоток").description("стальной молоток").available(true)
                .owner(owner).build();
        tem.persist(item);
        User booker = User.builder().name("booker").email("booker@example.com").build();
        tem.persist(booker);
        Booking booking = Booking.builder()
                .item(item).booker(booker)
                .start(now.plusDays(3)).end(now.plusDays(5))
                .status(BookingStatus.APPROVED)
                .build();
        tem.persist(booking);

        // By Owner
        Booking bookingReturned = bookingRepository
                .findBookingByOwnerOrBooker(booking.getId(), owner.getId()).orElse(null);
        Assertions.assertThat(bookingReturned).isNotNull()
                .isEqualTo(booking);

        // By Owner
        Booking bookingReturned2 = bookingRepository
                .findBookingByOwnerOrBooker(booking.getId(), booker.getId()).orElse(null);
        Assertions.assertThat(bookingReturned2).isNotNull()
                .isEqualTo(booking);


        // Wrong BookingId
        Booking bookingReturned3 = bookingRepository
                .findBookingByOwnerOrBooker(99L, owner.getId()).orElse(null);
        Assertions.assertThat(bookingReturned3).isNull();

        // Wrong BookerId or OwnerId
        Booking bookingReturned4 = bookingRepository
                .findBookingByOwnerOrBooker(booking.getId(), 99L).orElse(null);
        Assertions.assertThat(bookingReturned4).isNull();
    }

    @Test
    void test_findBookingForApprove() {
        User owner = User.builder().name("owner").email("owner@example.com").build();
        tem.persist(owner);
        Item item = Item.builder()
                .name("молоток").description("стальной молоток").available(true)
                .owner(owner).build();
        tem.persist(item);
        User booker = User.builder().name("booker").email("booker@example.com").build();
        tem.persist(booker);
        Booking booking = Booking.builder()
                .item(item).booker(booker)
                .start(now.plusDays(3)).end(now.plusDays(5))
                .status(BookingStatus.APPROVED)
                .build();
        tem.persist(booking);

        // Correct
        Booking bookingRet = bookingRepository
                .findBookingForApprove(booking.getId(), owner.getId()).orElse(null);
        Assertions.assertThat(bookingRet).isNotNull()
                .isEqualTo(booking);

        // Wrong owner
        Booking bookingRet2 = bookingRepository
                .findBookingForApprove(booking.getId(), 99L).orElse(null);
        Assertions.assertThat(bookingRet2).isNull();
    }
}
