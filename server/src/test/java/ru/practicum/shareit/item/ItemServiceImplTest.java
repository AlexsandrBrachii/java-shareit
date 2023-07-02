package ru.practicum.shareit.item;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private ItemDto itemDto;
    private Item item;
    private ItemDto itemDtoMustBe;
    private ItemBookingDto itemBookingDtoMustBe;
    private Item item2;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L).name("user").email("user@example.com")
                .build();
        itemDto = ItemDto.builder()
                .name("hammer").description("good steel hammer").available(true)
                .build();
        item = Item.builder()
                .id(1L)
                .name("hammer").description("good steel hammer").available(true)
                .owner(user)
                .build();
        itemDtoMustBe = ItemDto.builder()
                .id(1L)
                .name("hammer").description("good steel hammer").available(true)
                .requestId(null)
                .build();
        itemBookingDtoMustBe = ItemBookingDto.builder()
                .id(1L)
                .name("hammer").description("good steel hammer").available(true)
                .comments(new ArrayList<>())
                .lastBooking(null)
                .nextBooking(null)
                .build();
        item2 = Item.builder()
                .id(2L)
                .name("drill").description("best drill").available(false)
                .owner(user)
                .build();
    }

    @Test
    void createItem_Normal_WithoutRequestId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto added = itemService.createItem(1L, itemDto);

        Assertions.assertThat(added)
                .isNotNull()
                .usingRecursiveComparison().isEqualTo(itemDtoMustBe);
        Assertions.assertThat(added.getRequestId()).isNull();

        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(itemRepository, Mockito.times(1)).save(any(Item.class));
        Mockito.verifyNoMoreInteractions(userRepository, itemRepository);
    }

    @Test
    void createItem_Normal_WithRequestId() {
        itemDto.setRequestId(1L);
        itemDtoMustBe.setRequestId(1L);

        User requestor = User.builder()
                .id(2L).name("requestor").email("requestor@example.com")
                .build();


        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .items(List.of(item))
                .requestor(requestor)
                .description("good hummer")
                .created(LocalDateTime.now())
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));
        item.setRequest(itemRequest);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto added = itemService.createItem(1L, itemDto);


        Assertions.assertThat(added)
                .isNotNull()
                .usingRecursiveComparison().isEqualTo(itemDtoMustBe);
        Assertions.assertThat(added.getRequestId()).isEqualTo(itemRequest.getId());

        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(itemRepository, Mockito.times(1)).save(any(Item.class));
        Mockito.verify(itemRequestRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verifyNoMoreInteractions(userRepository, itemRepository, itemRequestRepository);
    }

    @Test
    void createItem_NotExistUser_ReturnNotFoundException() {
        when(userRepository.findById(999L)).thenThrow(NotFoundException.class);

        Throwable thrown = Assertions.catchException(() -> itemService.createItem(999L, itemDto));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class);

        Mockito.verify(userRepository, Mockito.times(1)).findById(999L);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verifyNoInteractions(itemRepository, itemRequestRepository);
    }

    @Test
    void createItem_NotExistItemRequest_ReturnNotFoundException() {
        itemDto.setRequestId(999L);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(999L))
                .thenThrow(NotFoundException.class);

        Throwable thrown = Assertions.catchException(() -> itemService.createItem(1L, itemDto));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class);

        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(itemRequestRepository, Mockito.times(1)).findById(999L);
        Mockito.verifyNoMoreInteractions(userRepository, itemRequestRepository);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void updateItem_ItemNotFound_ReturnNotFoundException() {
        ItemDto dataToUpdate = itemDto.toBuilder()
                .id(999L)
                .name("hammer updated")
                .description("good steel hammer updated")
                .available(false)
                .build();

        when(itemRepository.findById(anyLong())).thenThrow(NotFoundException.class);

        Throwable thrown = Assertions.catchException(() -> itemService.updateItem(1L, dataToUpdate));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class);

        Mockito.verify(itemRepository, Mockito.times(1)).findById(999L);
        verifyNoMoreInteractions(itemRepository);
    }

    /*@Test
    void findItems_WithEmptyText_ReturnEmptyList() {
        List<ItemDto> result = itemService.findItems("");

        Assertions.assertThat(result)
                .hasSize(0).isEqualTo(new ArrayList<ItemDto>());

        verifyNoInteractions(itemRepository);
    }*/

    @Test
    void createComment_WithEmptyCommentTest_ReturnBadRequestException() {
        CommentNewDto commentNewDto = CommentNewDto.builder()
                .text("")
                .build();

        Throwable thrown = Assertions.catchException(() -> itemService
                .addComment(1L, 1L, commentNewDto));

        Assertions.assertThat(thrown)
                .isInstanceOf(ValidationException.class)
                .hasMessage("Текст комментария не должен быть пустым.");

        Mockito.verifyNoInteractions(bookingRepository, commentRepository);
    }

    @Test
    void createComment_WhenUserDoNotTakeItem_ReturnBadRequestException() {
        CommentNewDto commentNewDto = CommentNewDto.builder()
                .text("New comment")
                .build();

        when(bookingRepository.findBookingForComment(anyLong(), anyLong(),
                any(LocalDateTime.class), any(BookingStatus.class), any(PageRequest.class)))
                .thenReturn(Page.empty());

        Throwable thrown = Assertions.catchException(() -> itemService
                .addComment(1L, 1L, commentNewDto));

        Assertions.assertThat(thrown)
                .isInstanceOf(ValidationException.class)
                .hasMessage("Не верный запрос на комментарий");

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findBookingForComment(anyLong(), anyLong(), any(LocalDateTime.class),
                        any(BookingStatus.class), any(PageRequest.class));
        Mockito.verifyNoMoreInteractions(bookingRepository);
        verifyNoInteractions(commentRepository);
    }

    @Test
    void createComment_Normal() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = Booking.builder()
                .id(1L)
                .start(now.minusDays(2))
                .end(now.minusDays(1))
                .status(BookingStatus.APPROVED)
                .item(item)
                .booker(user)
                .build();

        CommentNewDto commentNewDto = CommentNewDto.builder()
                .text("New comment")
                .build();

        Comment comment = Comment.builder()
                .item(item)
                .created(now)
                .text("New comment")
                .author(user)
                .build();

        CommentDto commentDtoMustBe = CommentMapper.commentToDto(comment);

        Page<Booking> page = new PageImpl<>(List.of(booking));

        when(bookingRepository.findBookingForComment(anyLong(), anyLong(),
                any(LocalDateTime.class), any(BookingStatus.class), any(PageRequest.class)))
                .thenReturn(page);
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        CommentDto result = itemService.addComment(1L, 1L, commentNewDto);

        Assertions.assertThat(result)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(commentDtoMustBe);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findBookingForComment(anyLong(), anyLong(), any(LocalDateTime.class),
                        any(BookingStatus.class), any(PageRequest.class));
        Mockito.verify(commentRepository, Mockito.times(1))
                .save(any(Comment.class));
        Mockito.verifyNoMoreInteractions(bookingRepository, commentRepository);
    }
}
