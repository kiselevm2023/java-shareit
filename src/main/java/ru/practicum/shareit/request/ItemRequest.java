package ru.practicum.shareit.request;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class ItemRequest {

    private final Long id;
    private final String description;
    private final Long requestor;
    private LocalDateTime created;
}