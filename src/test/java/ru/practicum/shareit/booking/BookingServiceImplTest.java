package ru.practicum.shareit.booking;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.utils.Utils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private Item item;
    private BookingNewDto bookingNewDto;
    private Booking booking;
    private BookingDto bookingDtoMastBe;

    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L).name("user").email("user@example.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("молоток")
                .description("стальной молоток")
                .owner(new User(2L, "owner", "owner@example.com"))
                .available(true)
                .build();

        bookingNewDto = BookingNewDto.builder()
                .itemId(1L)
                .start(now.plusDays(2))
                .end(now.plusDays(3))
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(now.plusDays(2))
                .end(now.plusDays(3))
                .booker(user)
                .item(item)
                .status(BookingStatus.WAITING)
                .build();

        bookingDtoMastBe = BookingDto.builder()
                .id(1L)
                .booker(UserMapper.userToDto(user))
                .item(ItemMapper.itemToDto(item))
                .status(BookingStatus.WAITING.name())
                .start(now.plusDays(2).format(Utils.dtFormatter))
                .end(now.plusDays(3).format(Utils.dtFormatter))
                .build();
    }

    @Test
    void createBooking_WrongUserId_ReturnNotFoundException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Throwable throwable = Assertions.catchException(() -> bookingService.createBooking(99L, bookingNewDto));

        Assertions.assertThat(throwable)
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь c id=99 не найден.");

        Mockito.verify(userRepository, times(1)).findById(anyLong());
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void createBooking_WrongItemId_ReturnNotFoundException() {
        bookingNewDto.setItemId(99L);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findByIdAndOwnerNot(anyLong(), any(User.class)))
                .thenReturn(Optional.empty());

        Throwable throwable = Assertions.catchException(() -> bookingService.createBooking(1L, bookingNewDto));

        Assertions.assertThat(throwable)
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Вещь c id=99 не найдена.");

        Mockito.verify(userRepository, times(1)).findById(anyLong());
        Mockito.verify(itemRepository, times(1))
                .findByIdAndOwnerNot(anyLong(), any(User.class));
        Mockito.verifyNoMoreInteractions(userRepository, itemRepository);
    }

    @Test
    void createBooking_ItemAvailableFalse_ReturnBadRequestException() {
        item.setAvailable(false);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findByIdAndOwnerNot(anyLong(), any(User.class)))
                .thenReturn(Optional.of(item));

        Throwable throwable = Assertions.catchException(() -> bookingService.createBooking(1L, bookingNewDto));

        Assertions.assertThat(throwable)
                .isInstanceOf(ValidationException.class)
                .hasMessage("Вещь c id=1 не доступна для бронирования.");
    }

    @Test
    void createBooking_Normal() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findByIdAndOwnerNot(anyLong(), any(User.class)))
                .thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingDto saved = bookingService.createBooking(1L, bookingNewDto);

        Assertions.assertThat(saved).isNotNull();
        Assertions.assertThat(saved.getId()).isEqualTo(1L);
        Assertions.assertThat(saved.getBooker().getId()).isEqualTo(1L);
        Assertions.assertThat(saved.getItem().getId()).isEqualTo(1L);

        Assertions.assertThat(saved)
                .usingRecursiveComparison()
                .isEqualTo(bookingDtoMastBe);
    }

    @Test
    void approve_WrongOwnerOrAndBookingId_ReturnNotFoundException() {
        when(bookingRepository.findBookingForApprove(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        Throwable throwable = Assertions.catchException(() -> bookingService
                .approve(99L, true, 100L));

        Assertions.assertThat(throwable)
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Бронирование c id=100 не найден.");
    }

    @Test
    void approve_WhenApproveAndStatusApproved_ReturnNotFoundException() {
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findBookingForApprove(anyLong(), anyLong()))
                .thenReturn(Optional.of(booking));

        Throwable throwable = Assertions.catchException(() -> bookingService
                .approve(1L, true, 1L));

        Assertions.assertThat(throwable)
                .isInstanceOf(ValidationException.class)
                .hasMessage("Бронирование уже имеет устанавливаемый статус.");
    }

    /*@Test
    void approve_NotOwner_ReturnNotFoundException() {

        when(bookingRepoitory.findBookingForApprove(anyLong(), anyLong()))
                .thenReturn(Optional.of(booking));

        Throwable throwable = Assertions.catchException(() -> bookingService
                .approve(1L, true, 1L));

        Assertions.assertThat(throwable)
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("У вас нет прав для изменения статуса бронирования.");
    }*/

    @Test
    void approve_Normal_Approve() {
        when(bookingRepository.findBookingForApprove(anyLong(), anyLong()))
                .thenReturn(Optional.of(booking));
        Booking updated = booking.toBuilder()
                .status(BookingStatus.APPROVED)
                .build();
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(updated);

        BookingDto result = bookingService.approve(2L, true, 1L);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED.name());

    }

    @Test
    void approve_Normal_Reject() {
        when(bookingRepository.findBookingForApprove(anyLong(), anyLong()))
                .thenReturn(Optional.of(booking));
        Booking updated = booking.toBuilder()
                .status(BookingStatus.REJECTED)
                .build();
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(updated);

        BookingDto result = bookingService.approve(2L, false, 1L);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatus()).isEqualTo(BookingStatus.REJECTED.name());
    }

    @Test
    void getBooking_WrongBookingOrBookerOrOwner_ReturnNotFoundException() {
        when(bookingRepository.findBookingByOwnerOrBooker(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        Throwable throwable = Assertions.catchException(() -> bookingService.getBooking(99L, 100L));

        Assertions.assertThat(throwable)
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Бронирование c id=100 не найден.");
    }

    @Test
    void getBooking_Normal() {
        when(bookingRepository.findBookingByOwnerOrBooker(anyLong(), anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBooking(1L, 1L);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).usingRecursiveComparison()
                .isEqualTo(bookingDtoMastBe);
    }

    @Test
    void getAllBookings_WrongUserId() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        Throwable throwable = Assertions.catchException(() -> bookingService
                .getAllBookings(99L, "ALL", 0, 20));

        Assertions.assertThat(throwable)
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь c id=99 не найден.");
    }

    @Test
    void getAllBookings_Normal_Stateless() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndStatusInOrderByStartDesc(anyLong(),
                anySet(), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndStatusEqualsOrderByStartDesc(anyLong(),
                any(BookingStatus.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> listAll = bookingService.getAllBookings(1L, "ALL", 0, 20);
        Assertions.assertThat(listAll).hasSize(1);

        List<BookingDto> listFuture = bookingService.getAllBookings(1L, "FUTURE", 0, 20);
        Assertions.assertThat(listFuture).hasSize(1);

        List<BookingDto> listPast = bookingService.getAllBookings(1L, "PAST", 0, 20);
        Assertions.assertThat(listPast).hasSize(1);

        List<BookingDto> listReject = bookingService.getAllBookings(1L, "REJECTED", 0, 20);
        Assertions.assertThat(listReject).hasSize(1);

        List<BookingDto> listCurrent = bookingService.getAllBookings(1L, "CURRENT", 0, 20);
        Assertions.assertThat(listCurrent).hasSize(1);

        List<BookingDto> listWaiting = bookingService.getAllBookings(1L, "WAITING", 0, 20);
        Assertions.assertThat(listWaiting).hasSize(1);
    }

    @Test
    void getAllBookingsForOwner_WrongUserId() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        Throwable throwable = Assertions.catchException(() -> bookingService
                .getAllBookingsForOwner(99L, "ALL", 0, 20));

        Assertions.assertThat(throwable)
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь c id=99 не найден.");
    }

    @Test
    void getAllBookingsForOwner_Normal_Stateless() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByItemOwnerIdAndStatusInOrderByStartDesc(anyLong(),
                anySet(), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByItemOwnerIdAndStatusEqualsOrderByStartDesc(anyLong(),
                any(BookingStatus.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> listAll = bookingService.getAllBookingsForOwner(1L, "ALL", 0, 20);
        Assertions.assertThat(listAll).hasSize(1);

        List<BookingDto> listFuture = bookingService.getAllBookingsForOwner(1L, "FUTURE", 0, 20);
        Assertions.assertThat(listFuture).hasSize(1);

        List<BookingDto> listPast = bookingService.getAllBookingsForOwner(1L, "PAST", 0, 20);
        Assertions.assertThat(listPast).hasSize(1);

        List<BookingDto> listReject = bookingService.getAllBookingsForOwner(1L, "REJECTED", 0, 20);
        Assertions.assertThat(listReject).hasSize(1);

        List<BookingDto> listCurrent = bookingService.getAllBookingsForOwner(1L, "CURRENT", 0, 20);
        Assertions.assertThat(listCurrent).hasSize(1);

        List<BookingDto> listWaiting = bookingService.getAllBookingsForOwner(1L, "WAITING", 0, 20);
        Assertions.assertThat(listWaiting).hasSize(1);
    }
}
