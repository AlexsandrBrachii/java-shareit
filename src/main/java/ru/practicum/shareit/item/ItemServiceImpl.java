package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentNewDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {


    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Override
    @Transactional
    public ItemDto createItem(Long idUser, ItemDto itemDto) {
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь c id=%d не найдена", idUser)));
        Item item = ItemMapper.dtoToItem(itemDto);
        item.setOwner(user);
        Item saved = itemRepository.save(item);
        log.info("Пользователь id={} добавил вещь с id={}", idUser, saved.getId());
        return ItemMapper.itemToDto(saved);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long idUser, ItemDto itemDto) {
        Item itemUpdate = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь c id=%d не найдена", itemDto.getId())));

        if (idUser == null) {
            throw new RuntimeException("id пользователя не может быть пустым");
        }

        checkItemWithIdUser(idUser);
        if (itemDto.getName() != null) {
            log.info("Обновляется имя вещи с id={}.", itemUpdate.getId());
            itemUpdate.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            log.info("Обновляется описание вещи с id={}.", itemUpdate.getId());
            itemUpdate.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            log.info("Обновляется статус одобрения вещи с id={}.", itemUpdate.getId());
            itemUpdate.setAvailable(itemDto.getAvailable());
        }
        log.info("Пользователь id={} обновил вещь с id={}", idUser, itemUpdate.getId());
        return ItemMapper.itemToDto(itemRepository.save(itemUpdate));
    }

    @Override
    public ItemBookingDto getItemById(Long idUser, Long idItem) {
        Item item = itemRepository.findById(idItem)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь c id=%d не найдена", idItem)));
        List<Comment> comments = commentRepository.findCommentsByItemIdOrderByCreatedAsc(item.getId());
        return ItemMapper.toItemBookingDto(item,
                getLastBooking(item.getId(), idUser),
                getNextBooking(item.getId(), idUser),
                comments);
    }

    @Override
    public Collection<ItemBookingDto> getAllItems(Long idUser) {
        List<ItemBookingDto> returned = new ArrayList<>();
        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(idUser);
        for (Item item : items) {
            List<Comment> comments = commentRepository.findCommentsByItemIdOrderByCreatedAsc(item.getId());
            returned.add(ItemMapper.toItemBookingDto(item,
                    getLastBooking(item.getId(), idUser),
                    getNextBooking(item.getId(), idUser),
                    comments));
        }
        return returned;
    }

    @Override
    public List<ItemDto> findItems(String text) {
        if (text != null && text.isBlank()) {
            return new ArrayList<>();
        }
        return ItemMapper.itemToDto(itemRepository.search(text));
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentNewDto comment) {
        if (comment.getText() != null && comment.getText().isEmpty()) {
            throw new ValidationException("Текст комментария не должен быть пустым.");
        }
        LocalDateTime now = LocalDateTime.now();
        PageRequest pageRequest = PageRequest.of(0, 1);
        Page<Booking> page = bookingRepository.findBookingForComment(userId, itemId, now, BookingStatus.APPROVED, pageRequest);

        Booking booking = page.get().findFirst().orElseThrow(() -> new ValidationException("Не верный запрос на комментарий"));

        Comment commentNew = Comment.builder()
                .author(booking.getBooker())
                .text(comment.getText())
                .item(booking.getItem())
                .created(LocalDateTime.now())
                .build();
        Comment saved = commentRepository.save(commentNew);
        return CommentMapper.commentToDto(saved);
    }

    public void checkItemWithIdUser(Long idUser) {
        boolean foundIdUser = false;
        for (Item item1 : itemRepository.findAll()) {
            if (Objects.equals(item1.getOwner().getId(), idUser)) {
                foundIdUser = true;
                break;
            }
        }
        if (!foundIdUser) {
            throw new NotFoundException(String.format("У пользователя с id=%s не найдено вещей", idUser));
        }
    }

    private BookingShort getLastBooking(Long itemId, Long userId) {
        PageRequest page = PageRequest.of(0, 1);
        LocalDateTime now = LocalDateTime.now();
        List<BookingShort> lastList = bookingRepository.getLastBooking(itemId, userId,
                BookingStatus.APPROVED, now, page);
        if (lastList.size() > 0) {
            return lastList.get(0);
        }
        return null;
    }

    private BookingShort getNextBooking(Long itemId, Long userId) {
        PageRequest page = PageRequest.of(0, 1);
        LocalDateTime now = LocalDateTime.now();
        List<BookingShort> nextList = bookingRepository.getNextBooking(itemId, userId,
                BookingStatus.APPROVED, now, page);
        if (nextList.size() > 0) {
            return nextList.get(0);
        }
        return null;
    }
}
