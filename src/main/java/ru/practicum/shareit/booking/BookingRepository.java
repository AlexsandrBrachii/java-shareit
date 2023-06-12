package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingShort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // проверяем, было ли бронирование вещи пользователем
    @Query("select b from Booking as b " +
            "where b.booker.id=?1 and b.item.id=?2 and " +
            "b.end<?3 and b.status=?4 order by b.end asc ")
    Page<Booking> findBookingForComment(Long bookerId, Long itemId,
                                        LocalDateTime now, BookingStatus status, Pageable pageable);

    @Query("select b " +
            "from Booking as b " +
            "where b.id=?1 and( b.booker.id=?2 or b.item.owner.id=?2 )")
    Optional<Booking> findBookingByOwnerOrBooker(Long id, Long finderId);

    @Query("select b from Booking as b where b.id=?1 and b.item.owner.id=?2")
    Optional<Booking> findBookingForApprove(Long bookingId, Long userId);

    @Query("select new ru.practicum.shareit.booking.dto.BookingShort(b.id, b.booker.id) " +
            "from Booking as b " +
            "where b.item.id=?1 and b.item.owner.id=?2 and b.status=?3 and b.start>?4 " +
            "order by b.start asc")
    List<BookingShort> getNextBooking(Long itemId, Long userId, BookingStatus status,
                                      LocalDateTime now, Pageable pageable);

    @Query("select new ru.practicum.shareit.booking.dto.BookingShort(b.id, b.booker.id) " +
            "from Booking as b " +
            "where b.item.id=?1 and b.item.owner.id=?2 and b.status=?3 and b.start<?4 " +
            "order by b.start desc")
    List<BookingShort> getLastBooking(Long itemId, Long userId, BookingStatus status,
                                      LocalDateTime now, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "JOIN i.owner u " +
            "WHERE u.id = :ownerId " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByItemOwner_IdOrderByStartDesc(Long ownerId);

    // для ALL
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    // для ALL если owner
    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    // для FUTURE
    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime dateTime);

    // для FUTURE если owner
    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime dateTime);

    // для WAITING
    List<Booking> findAllByBookerIdAndStatusEqualsOrderByStartDesc(Long bookerId, BookingStatus status);

    // для WAITING если owner
    List<Booking> findAllByItemOwnerIdAndStatusEqualsOrderByStartDesc(Long ownerId, BookingStatus status);

    // для REJECTED
    List<Booking> findAllByBookerIdAndStatusInOrderByStartDesc(Long bookerId, Set<BookingStatus> statusSet);

    // для REJECTED если owner
    List<Booking> findAllByItemOwnerIdAndStatusInOrderByStartDesc(Long ownerId, Set<BookingStatus> statusSet);

    // для CURRENT
    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime now1, LocalDateTime now2);

    // для CURRENT если owner
    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime now1, LocalDateTime now2);

    // для PAST
    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    // для PAST если owner
    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);
}