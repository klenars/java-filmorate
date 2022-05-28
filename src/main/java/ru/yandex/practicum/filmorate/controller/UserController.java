package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    int idForUsers = 1;
    Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public User addUser(@RequestBody User user) {
        int id = idForUsers++;
        user.setId(id);

        if (userValidation(user)) {
            users.put(id, user);
            log.info("Added user name: {}, id: {}", user.getName(), user.getId());
            return user;
        }
        return null;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        int id = user.getId();
        if (!users.containsKey(id)) {
            log.warn("Unknown ID for update user!");
            throw new ValidationException("Unknown ID for update user!");
        }
        if (userValidation(user)) {
            users.put(id, user);
            log.info("Updated user id: {}", user.getId());
            return user;
        }
        return null;
    }

    @GetMapping
    public List<User> getAllFilms() {
        return new ArrayList<>(users.values());
    }

    private boolean userValidation(User user) {
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }

        String errorMessage = null;

        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            errorMessage = "E-mail is wrong!";
        } else if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            errorMessage = "Login is wrong!";
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            errorMessage = "Birthday can't be in the future!";
        }

        if (errorMessage != null) {
            log.warn(errorMessage);
            throw new ValidationException(errorMessage);
        } else {
            return true;
        }
    }
}
