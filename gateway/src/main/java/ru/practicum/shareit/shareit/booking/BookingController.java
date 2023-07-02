package ru.practicum.shareit.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.shareit.booking.dto.BookingNewRequestDto;
import ru.practicum.shareit.shareit.exceptions.BadRequestException;

import javax.validation.Valid;

import static ru.practicum.shareit.shareit.utils.Utils.checkPaging;

@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @RequestBody @Valid BookingNewRequestDto bookingNewDto
    ) {
        validDateForBookingNewDto(bookingNewDto);
        return bookingClient.create(userId, bookingNewDto);
    }

    @PatchMapping(value = "/{bookingId}", params = "approved")
    public ResponseEntity<Object> approve(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @PathVariable long bookingId,
            @RequestParam boolean approved
    ) {
        return bookingClient.approve(userId, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> get(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @PathVariable long bookingId
    ) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size
    ) {
        checkPaging(from, size);
        return bookingClient.getAllBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerAll(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size
    ) {
        checkPaging(from, size);
        return bookingClient.getAllBookingsForOwner(userId, state, from, size);
    }

    // Проверка бронирования
    private void validDateForBookingNewDto(BookingNewRequestDto bookingNewDto) {

        if (bookingNewDto.getStart().isEqual(bookingNewDto.getEnd())) {
            throw new BadRequestException("Дата начала бронирования не должна совпадать " +
                    "с датой окончания бронирования.");
        }

        if (bookingNewDto.getStart().isAfter(bookingNewDto.getEnd())) {
            throw new BadRequestException("Дата начала бронирования не должна быть " +
                    "позже даты окончания бронирования.");
        }
    }
}

