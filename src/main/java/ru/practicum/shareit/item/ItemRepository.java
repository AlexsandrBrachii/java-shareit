package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // Список вещей пользователя
    List<Item> findAllByOwnerIdOrderByIdAsc(Long ownerId);

    // Найти вещь по ID и не принадлежащую владельцу
    Optional<Item> findByIdAndOwnerNot(Long itemId, User user);

    // Поиск вещий по названию или описанию
    @Query("select i from Item i " +
            "where i.available=true and (upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%')))")
    List<Item> search(String text);
}
