package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;


import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {
    private final UserDbStorage userStorage;

    @Test
    public void testFindUserById() {
        User testUser = new User();
        testUser.setName("testName");
        testUser.setLogin("testLogin");
        testUser.setEmail("test@email.ru");
        testUser.setBirthday(LocalDate.of(1984, 7, 15));
        userStorage.add(testUser);

        User user = userStorage.get(1);

        assertThat(user).hasFieldOrPropertyWithValue("id", 1);
    }
} 