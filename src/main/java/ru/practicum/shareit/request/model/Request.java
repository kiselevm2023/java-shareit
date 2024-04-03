package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@RequiredArgsConstructor
@Table(name = "requests")
@AllArgsConstructor
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id ")
    private long id;
    private String name;
    private String description;
    private LocalDateTime created;
    @Column(name = "item_id")
    private int itemId;
    private boolean available;
    @Column(name = "user_id")
    private long userId;
}