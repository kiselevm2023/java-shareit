package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Item {

    private long id;
    private String name;
    private String description;
    private Boolean available;
    private long owner;
    private String request;
}
