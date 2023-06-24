package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(long userId, ItemRequestDto requestDto);

    List<ItemRequestDto> findAllByRequestor(long userId);

    List<ItemRequestDto> findItemRequests(long userId, int from, int size);

    ItemRequestDto getItemRequest(long userId, long itemRequestId);
}
