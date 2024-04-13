package ru.practicum.shareit.item.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
//import ru.practicum.shareit.validated.Create;

import javax.persistence.*;
//import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text", nullable = false)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column
    private LocalDateTime created;

    @PrePersist
    public void prePersist() {
        if (created == null) {
            created = LocalDateTime.now();
        }
    }
}