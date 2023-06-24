package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {

    public static ItemRequest dtoToItemRequest(User user, ItemRequestDto requestDto) {
        return ItemRequest.builder()
                .requestor(user)
                .description(requestDto.getDescription())
                .created(LocalDateTime.now())
                .build();
    }

    public static ItemRequestDto itemRequestToDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemRequest.getItems() != null ? ItemMapper.itemToDto(itemRequest.getItems()) : new ArrayList<>())
                .build();
    }

    public static List<ItemRequestDto> itemRequestToDto(List<ItemRequest> requestList) {
        return requestList.stream()
                .map(ItemRequestMapper::itemRequestToDto)
                .collect(Collectors.toList());
    }
}
