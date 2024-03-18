package ru.practicum.shareit.exception;

public class AlreadyExsist extends RuntimeException {

    public AlreadyExsist(String message) {
        super(message);
    }
}
