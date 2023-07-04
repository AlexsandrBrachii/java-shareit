package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ItemRequestDto createItemRequest(long userId, ItemRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь c id=%d не найдена.", userId)));
        ItemRequest itemRequest = ItemRequestMapper.dtoToItemRequest(user, requestDto);
        ItemRequest saved = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.itemRequestToDto(saved);
    }

    @Override
    public List<ItemRequestDto> findAllByRequestor(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь c id=%d не найдена.", userId));
        }
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorId(userId);
        return ItemRequestMapper.itemRequestToDto(itemRequests);
    }

    @Override
    public List<ItemRequestDto> findItemRequests(long userId, int from, int size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId, pageRequest);
        return ItemRequestMapper.itemRequestToDto(itemRequests);
    }

    @Override
    public ItemRequestDto getItemRequest(long userId, long itemRequestId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь c id=%d не найдена.", userId));
        }
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос c id=%d не найдена.", itemRequestId)));
        return ItemRequestMapper.itemRequestToDto(itemRequest);
    }
}
