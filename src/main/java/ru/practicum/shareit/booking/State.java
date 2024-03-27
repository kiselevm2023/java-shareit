package ru.practicum.shareit.booking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.practicum.shareit.exception.ValidationException;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    private static final Logger log = LoggerFactory.getLogger(State.class);
    static State checkState(String state) {
        log.info("Сервис: валидация состояния бронирования");
        for (State s : State.values()) {
            if (s.name().equalsIgnoreCase(state)) {
                return s;
            }
        }
        log.error("Unknown state: {}", state);
        throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
    }
}