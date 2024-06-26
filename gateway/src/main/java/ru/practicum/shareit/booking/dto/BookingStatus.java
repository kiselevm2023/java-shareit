package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.exception.UnsupportedStatusException;

public enum BookingStatus {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED,
    CURRENT,
    ALL,
    PAST,
    FUTURE;

    public static BookingStatus fromString(String value) {
        for (BookingStatus status : values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new UnsupportedStatusException("Unknown state: " + value);
    }
}