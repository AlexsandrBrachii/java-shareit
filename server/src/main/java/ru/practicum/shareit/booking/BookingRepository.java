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
    Page<BookingShort> getNextBooking(Long itemId, Long userId, BookingStatus status,
                                      LocalDateTime now, Pageable pageable);

    @Query("select new ru.practicum.shareit.booking.dto.BookingShort(b.id, b.booker.id) " +
            "from Booking as b " +
            "where b.item.id=?1 and b.item.owner.id=?2 and b.status=?3 and b.start<?4 " +
            "order by b.start desc")
    Page<BookingShort> getLastBooking(Long itemId, Long userId, BookingStatus status,
                                      LocalDateTime now, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "JOIN i.owner u " +
            "WHERE u.id = :ownerId " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByItemOwner_IdOrderByStartDesc(Long ownerId);

    // для ALL
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    // для ALL если owner
    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable pageable);

    // для FUTURE
    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime dateTime, Pageable pageable);

    // для FUTURE если owner
    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime dateTime, Pageable pageable);

    // для WAITING
    List<Booking> findAllByBookerIdAndStatusEqualsOrderByStartDesc(Long bookerId, BookingStatus status, Pageable pageable);

    // для WAITING если owner
    List<Booking> findAllByItemOwnerIdAndStatusEqualsOrderByStartDesc(Long ownerId, BookingStatus status, Pageable pageable);

    // для REJECTED
    List<Booking> findAllByBookerIdAndStatusInOrderByStartDesc(Long bookerId, Set<BookingStatus> statusSet, Pageable pageable);

    // для REJECTED если owner
    List<Booking> findAllByItemOwnerIdAndStatusInOrderByStartDesc(Long ownerId, Set<BookingStatus> statusSet, Pageable pageable);

    // для CURRENT
    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime now1, LocalDateTime now2, Pageable pageable);

    // для CURRENT если owner
    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime now1, LocalDateTime now2, Pageable pageable);

    // для PAST
    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now, Pageable pageable);

    // для PAST если owner
    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now, Pageable pageable);
}