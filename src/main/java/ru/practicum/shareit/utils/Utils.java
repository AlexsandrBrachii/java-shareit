package ru.practicum.shareit.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.exception.BadRequestException;

import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {

    //Если id пользователя null, то вызываем исключение BadRequestException
    public static void userIsNull(Long userId) {
        if (userId == null) throw new BadRequestException("Не известен пользователь.");
    }

    public static final DateTimeFormatter dtFormatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    //Проверка параметров запроса для постраничной обработки
    public static void checkPaging(int from, int size) {
        if ((from == 0 && size == 0) || (from < 0 && size > 0) ||
                (from == 0 && size < 0)) {
            throw new BadRequestException("Не верный запрос для постраничного вывода.");
        }
    }
}
