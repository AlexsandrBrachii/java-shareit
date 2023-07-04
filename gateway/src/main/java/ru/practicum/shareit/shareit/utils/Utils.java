package ru.practicum.shareit.shareit.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.shareit.exceptions.BadRequestException;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {
    /**
     * Проверка параметров запроса для постраничной обработки
     *
     * @param from индекс элемента с которого начинать пейджинация
     * @param size количество элементов
     */
    public static void checkPaging(int from, int size) {
        if ((from == 0 && size == 0) || (from < 0 && size > 0) ||
                (from == 0 && size < 0)) {
            throw new BadRequestException("Не верный запрос для постраничного вывода.");
        }
    }
}
