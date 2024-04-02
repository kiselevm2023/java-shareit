package ru.practicum.shareit.exception;

public class NotEnoughRules extends RuntimeException {

    public NotEnoughRules(String message) {
        super(message);
    }
}