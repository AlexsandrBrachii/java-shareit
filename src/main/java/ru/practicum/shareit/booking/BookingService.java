package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(Long userId, BookingNewDto bookingNewDto);

    BookingDto approve(Long userId, boolean approve, Long bookingId);

    BookingDto getBooking(Long userId, Long bookingId);
    List<BookingDto> getAllBookings(long userId, String stateS, int from, int size);

    List<BookingDto> getAllBookingsForOwner(long userId, String stateS, int from, int size);}
