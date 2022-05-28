package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            return user;
        }
        return null;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        int id = user.getId();
        if (!users.containsKey(id)) {
            throw new ValidationException("Unknown ID for update user!");
        }
        if (userValidation(user)) {
            users.put(id, user);
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

        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("E-mail is wrong!");
        } else if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Login is wrong!");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Birthday can't be in the future!");
        } else {
            return true;
        }
    }
}
