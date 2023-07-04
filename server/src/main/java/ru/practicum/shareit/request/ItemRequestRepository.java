package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    // Все запросы пользователя
    List<ItemRequest> findAllByRequestorId(long requestorId);

    // Все запросы за исключением запрашивающего
    List<ItemRequest> findAllByRequestorIdNotOrderByCreatedDesc(long requestorId, Pageable pageable);
}
