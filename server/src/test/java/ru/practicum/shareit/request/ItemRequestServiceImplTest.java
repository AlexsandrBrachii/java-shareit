package ru.practicum.shareit.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemRequestServiceImpl service;

    LocalDateTime created =
            LocalDateTime.of(2023, 5, 26, 10, 10, 0);

    ItemRequestDto requestDto;
    User requestor;
    ItemRequest savedItemRequest;

    @BeforeEach
    void setUp() {
        requestDto = ItemRequestDto.builder()
                .description("want hammer")
                .build();

        requestor = User.builder()
                .id(1L)
                .name("user")
                .email("user@example1")
                .build();

        savedItemRequest = ItemRequest.builder()
                .id(1L)
                .description("want hammer")
                .requestor(requestor)
                .created(created)
                .items(new ArrayList<>())
                .build();
    }

    @Test
    void createItemRequest_Normal() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(requestor));
        Mockito.when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(savedItemRequest);

        ItemRequestDto mustBe = ItemRequestMapper.itemRequestToDto(savedItemRequest);

        ItemRequestDto saved = service.createItemRequest(1L, requestDto);

        Assertions.assertThat(saved)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(mustBe);

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(itemRequestRepository, Mockito.times(1)).save(any(ItemRequest.class));
        Mockito.verifyNoMoreInteractions(userRepository, itemRequestRepository);
    }

    @Test
    void createItemRequest_WrongUser_ReturnCode404() {
        String message = String.format("Пользователь c id=%d не найдена.", 99L);

        Mockito.when(userRepository.findById(99L)).thenThrow(new NotFoundException(message));

        Throwable throwable = Assertions.catchException(() -> service.createItemRequest(99L, requestDto));

        Assertions.assertThat(throwable)
                .isInstanceOf(NotFoundException.class)
                .hasMessage(message);
    }

    @Test
    void findAllByRequestor_Normal() {

        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(itemRequestRepository.findAllByRequestorId(1L)).thenReturn(List.of(savedItemRequest));

        List<ItemRequestDto> returned = service.findAllByRequestor(1L);

        Assertions.assertThat(returned)
                .isNotNull()
                .hasSize(1);
        Assertions.assertThat(returned.get(0).getDescription()).isEqualTo(requestDto.getDescription());

        Mockito.verify(userRepository, Mockito.times(1)).existsById(1L);
        Mockito.verify(itemRequestRepository, Mockito.times(1)).findAllByRequestorId(1L);
        Mockito.verifyNoMoreInteractions(userRepository, itemRequestRepository);
    }

    @Test
    void findAllByRequestor_WrongUser_ReturnCode404() {
        String message = String.format("Пользователь c id=%d не найдена.", 99L);

        Mockito.when(userRepository.existsById(99L)).thenReturn(false);

        Throwable throwable = Assertions.catchException(() -> service.findAllByRequestor(99L));

        Assertions.assertThat(throwable)
                .isInstanceOf(NotFoundException.class)
                .hasMessage(message);

        Mockito.verify(userRepository, Mockito.times(1)).existsById(99L);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findItemRequests_Normal() {
        PageRequest pageRequest = PageRequest.of(0, 20);
        Mockito.when(itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(1L, pageRequest))
                .thenReturn(List.of(savedItemRequest));

        List<ItemRequestDto> retuned = service.findItemRequests(1L, 0, 20);

        Assertions.assertThat(retuned)
                .isNotNull()
                .hasSize(1);

        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findAllByRequestorIdNotOrderByCreatedDesc(1L, pageRequest);
        Mockito.verifyNoMoreInteractions(itemRequestRepository);
    }

    @Test
    void getItemRequest_Normal() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(itemRequestRepository.findById(1L))
                .thenReturn(Optional.of(savedItemRequest));

        ItemRequestDto mustBe = ItemRequestMapper.itemRequestToDto(savedItemRequest);

        ItemRequestDto returned = service.getItemRequest(1L, 1L);

        Assertions.assertThat(returned)
                .isNotNull()
                .usingRecursiveComparison().isEqualTo(mustBe);

        Mockito.verify(userRepository, Mockito.times(1)).existsById(1L);
        Mockito.verify(itemRequestRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(userRepository, itemRequestRepository);
    }

    @Test
    void getItemRequest_WrongUserId_ReturnCode404() {
        String message = String.format("Пользователь c id=%d не найдена.", 99L);

        Mockito.when(userRepository.existsById(99L)).thenReturn(false);

        Throwable throwable = Assertions.catchException(() -> service.getItemRequest(99L, 1L));

        Assertions.assertThat(throwable)
                .isInstanceOf(NotFoundException.class)
                .hasMessage(message);

        Mockito.verify(userRepository, Mockito.times(1)).existsById(99L);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getItemRequest_WrongItemRequestId_ReturnCode404() {
        String message = String.format("Запрос c id=%d не найдена.", 99L);

        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(itemRequestRepository.findById(99L))
                .thenThrow(new NotFoundException(message));

        Throwable throwable = Assertions.catchException(() -> service.getItemRequest(1L, 99L));

        Assertions.assertThat(throwable)
                .isInstanceOf(NotFoundException.class)
                .hasMessage(message);

        Mockito.verify(userRepository, Mockito.times(1)).existsById(1L);
        Mockito.verify(itemRequestRepository, Mockito.times(1)).findById(99L);
        Mockito.verifyNoMoreInteractions(userRepository, itemRequestRepository);
    }
}
