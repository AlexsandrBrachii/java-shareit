package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.utils.Utils;

import javax.validation.Valid;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemRequestDto requestDto) {
        log.info("POST /requests - добавление запроса на вещь пользователем {}", userId);
        return service.createItemRequest(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestDto> findAllByRequestor(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /requests - список запросов вещей пользователя {}", userId);
        return service.findAllByRequestor(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAll(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("GET /requests/all - список запросов");
        Utils.checkPaging(from, size);
        return service.findItemRequests(userId, from, size);
    }

    @GetMapping("/{id}")
    public ItemRequestDto getItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long id) {
        log.info("GET /requests/{} - информация о запросе", id);
        return service.getItemRequest(userId, id);
    }
}
