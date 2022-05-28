package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    static UserController userController = new UserController();

    @Test
    void addUser() {
        User user = new User();
        user.setName("TestUser");
        user.setLogin("TestUser");
        user.setEmail("123mail.ru");
        user.setBirthday(LocalDate.of(1984, 7, 15));
        assertThrows(ValidationException.class, () -> userController.addUser(user));

        user.setEmail("123@mail.ru");
        user.setLogin("Test User");
        assertThrows(ValidationException.class, () -> userController.addUser(user));

        user.setLogin("TestUser");
        user.setBirthday(LocalDate.of(2023, 1, 1));
        assertThrows(ValidationException.class, () -> userController.addUser(user));

        user.setBirthday(LocalDate.of(2020, 1, 1));
        assertDoesNotThrow(() -> userController.addUser(user));
    }

    @Test
    void updateUser() {
        User user = new User();
        user.setId(-1);

        assertThrows(ValidationException.class, () -> userController.updateUser(user));
    }
}