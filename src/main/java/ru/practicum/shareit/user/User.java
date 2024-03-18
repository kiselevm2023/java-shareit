package ru.practicum.shareit.user;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class User {

    private long id;
    private String name;
    private String email;
}