package ru.practicum.shareit.user.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.*;
import ru.practicum.shareit.validated.Create;
import ru.practicum.shareit.validated.Update;

@Entity
@Table(name = "users", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    //@NotBlank(groups = {Create.class}, message = "Email обязательное поле")
    @Email(groups = {Create.class, Update.class}, message = "Не верный формат email")
    @Column(name = "email", nullable = false, unique = true)
    private String email;
}