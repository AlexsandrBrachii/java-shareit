package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static Booking dtoToBooking(BookingNewDto bookingNewDto, Item item, User user) {
        return Booking.builder()
                .booker(user)
                .item(item)
                .start(bookingNewDto.getStart())
                .end(bookingNewDto.getEnd())
                .status(BookingStatus.WAITING)
                .build();
    }

    public static BookingDto bookingToDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart().format(Utils.dtFormatter))
                .end(booking.getEnd().format(Utils.dtFormatter))
                .status(booking.getStatus().name())
                .booker(UserMapper.userToDto(booking.getBooker()))
                .item(ItemMapper.itemToDto(booking.getItem()))
                .build();
    }

    public static List<BookingDto> bookingToDto(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::bookingToDto)
                .collect(Collectors.toList());
    }
}
