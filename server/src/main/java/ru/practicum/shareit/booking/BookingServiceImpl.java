package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingDto createBooking(Long userId, BookingNewDto bookingNewDto) {
        validDateForBookingNewDto(bookingNewDto);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь c id=%d не найден.", userId)));

        Item item = itemRepository.findByIdAndOwnerNot(bookingNewDto.getItemId(), user)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь c id=%d не найдена.", bookingNewDto.getItemId())));

        if (!item.getAvailable()) {
            throw new ValidationException(String.format("Вещь c id=%d не доступна для бронирования.", bookingNewDto.getItemId()));
        }
        Booking booking = BookingMapper.dtoToBooking(bookingNewDto, item, user);
        Booking saved = bookingRepository.save(booking);
        return BookingMapper.bookingToDto(saved);
    }

    @Override
    @Transactional
    public BookingDto approve(Long userId, boolean approve, Long bookingId) {
        Booking booking = bookingRepository.findBookingForApprove(bookingId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Бронирование c id=%d не найден.", bookingId)));
        if (approve && booking.getStatus() == BookingStatus.APPROVED) {
            throw new ValidationException("Бронирование уже имеет устанавливаемый статус.");
        }
        if (approve) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.bookingToDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findBookingByOwnerOrBooker(bookingId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Бронирование c id=%d не найден.", bookingId)));
        return BookingMapper.bookingToDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookings(long userId, String stateIn, int from, int size) {
        State state = getState(stateIn);
        checkUser(userId);
        PageRequest pageRequest = getPageRequest(from, size);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case WAITING:
                return BookingMapper.bookingToDto(bookingRepository
                        .findAllByBookerIdAndStatusEqualsOrderByStartDesc(userId,
                                BookingStatus.WAITING, pageRequest));
            case CURRENT:
                return BookingMapper.bookingToDto(bookingRepository
                        .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                                now, now, pageRequest));
            case REJECTED:
                Set<BookingStatus> statusSet = EnumSet.of(BookingStatus.REJECTED,
                        BookingStatus.CANCELED);
                return BookingMapper.bookingToDto(bookingRepository
                        .findAllByBookerIdAndStatusInOrderByStartDesc(userId, statusSet, pageRequest));
            case PAST:
                return BookingMapper.bookingToDto(bookingRepository
                        .findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now, pageRequest));
            case FUTURE:
                return BookingMapper.bookingToDto(bookingRepository
                        .findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now, pageRequest));
            case ALL:
            default:
                return BookingMapper.bookingToDto(bookingRepository
                        .findAllByBookerIdOrderByStartDesc(userId, pageRequest));
        }
    }


    @Override
    public List<BookingDto> getAllBookingsForOwner(long userId, String stateIn, int from, int size) {
        State state = getState(stateIn);
        checkUser(userId);
        PageRequest pageRequest = getPageRequest(from, size);
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case WAITING:
                return BookingMapper.bookingToDto(bookingRepository
                        .findAllByItemOwnerIdAndStatusEqualsOrderByStartDesc(userId, BookingStatus.WAITING, pageRequest));
            case CURRENT:
                return BookingMapper.bookingToDto(bookingRepository
                        .findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                                now, now, pageRequest));
            case REJECTED:
                Set<BookingStatus> statusSet = EnumSet.of(BookingStatus.REJECTED,
                        BookingStatus.CANCELED);
                return BookingMapper.bookingToDto(bookingRepository
                        .findAllByItemOwnerIdAndStatusInOrderByStartDesc(userId, statusSet, pageRequest));
            case PAST:
                return BookingMapper.bookingToDto(bookingRepository
                        .findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now, pageRequest));
            case FUTURE:
                return BookingMapper.bookingToDto(bookingRepository
                        .findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now, pageRequest));
            case ALL:
            default:
                return BookingMapper.bookingToDto(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, pageRequest));
        }
    }

    private void validDateForBookingNewDto(BookingNewDto bookingNewDto) {
        if (bookingNewDto.getStart() == null) {
            throw new ValidationException("Дата начала бронирования должны быть указано.");
        }
        if (bookingNewDto.getEnd() == null) {
            throw new ValidationException("Дата окончания бронирования должны быть указано.");
        }
        if (bookingNewDto.getStart().isEqual(bookingNewDto.getEnd())) {
            throw new ValidationException("Дата начала бронирования не должна совпадать " +
                    "с датой окончания бронирования.");
        }
        if (bookingNewDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата начала бронирования не должна быть " +
                    "в прошлом.");
        }
        if (bookingNewDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата окончания бронирования не должна быть " +
                    "в прошлом.");
        }
        if (bookingNewDto.getStart().isAfter(bookingNewDto.getEnd())) {
            throw new ValidationException("Дата начала бронирования не должна быть " +
                    "позже даты окончания бронирования.");
        }
    }

    private State getState(String state) {
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown state: " + state);
        }
    }

    private void checkUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь c id=%d не найден.", userId));
        }
    }

    private PageRequest getPageRequest(int from, int size) {
        int page = from / size;
        return PageRequest.of(page, size);
    }
}
