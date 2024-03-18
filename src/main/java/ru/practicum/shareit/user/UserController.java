package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.Check;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;



@RestController
@Slf4j
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserServiceImpl userServiceImpl) {
        this.userService = userServiceImpl;
    }

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("Request is received to get all users");
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable("userId") Long userId) {
        log.info("Request is received to get a user");
        return userService.getUserById(userId);
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Validated(Check.OnCreate.class) UserDto userDto) {
        log.info("Request is received to create a user");
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody @Validated(Check.class) UserDto user, @PathVariable("userId") Long userId) {
        log.info("Request is received to update a user");
        return userService.updateUser(user, userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {
        log.info("Request is received to delete a user");
        userService.deleteUser(userId);
    }
}