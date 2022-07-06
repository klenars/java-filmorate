package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Test
    void add() {
        User testUser1 = new User();
        testUser1.setName("testName1");
        testUser1.setLogin("testLogin1");
        testUser1.setEmail("test1@email.ru");
        testUser1.setBirthday(LocalDate.of(1984, 7, 15));
        userStorage.add(testUser1);

        assertTrue(userStorage.isExists(testUser1.getId()));
    }

    @Test
    void update() {
        User testUser2 = new User();
        testUser2.setName("testName2");
        testUser2.setLogin("testLogin2");
        testUser2.setEmail("test2@email.ru");
        testUser2.setBirthday(LocalDate.of(1980, 8, 16));
        userStorage.add(testUser2);

        User testUser3 = new User();
        testUser3.setName("testName3");
        testUser3.setLogin("testLogin3");
        testUser3.setEmail("test3@email.ru");
        testUser3.setBirthday(LocalDate.of(2020, 2, 22));

        testUser3.setId(testUser2.getId());

        userStorage.update(testUser3);

        User user = userStorage.get(testUser2.getId());

        assertThat(user).hasFieldOrPropertyWithValue("login", "testLogin3");
    }

    @Test
    void get() {
        User testUser3 = new User();
        testUser3.setName("testName3");
        testUser3.setLogin("testLogin3");
        testUser3.setEmail("test3@email.ru");
        testUser3.setBirthday(LocalDate.of(2020, 2, 22));
        userStorage.add(testUser3);

        User user = userStorage.get(testUser3.getId());

        assertThat(user).hasFieldOrPropertyWithValue("name", "testName3");
    }

    @Test
    void isExists() {
        User testUser1 = new User();
        testUser1.setName("testName1");
        testUser1.setLogin("testLogin1");
        testUser1.setEmail("test1@email.ru");
        testUser1.setBirthday(LocalDate.of(1984, 7, 15));
        userStorage.add(testUser1);

        assertFalse(userStorage.isExists(-1));
        assertTrue(userStorage.isExists(testUser1.getId()));
    }

    @Test
    void delete() {
        User testUser2 = new User();
        testUser2.setName("testName2");
        testUser2.setLogin("testLogin2");
        testUser2.setEmail("test2@email.ru");
        testUser2.setBirthday(LocalDate.of(1980, 8, 16));
        userStorage.add(testUser2);

        assertTrue(userStorage.isExists(testUser2.getId()));

        userStorage.delete(testUser2);

        assertFalse(userStorage.isExists(testUser2.getId()));
    }

    @Test
    void getAll() {
        User testUser1 = new User();
        testUser1.setName("testName1");
        testUser1.setLogin("testLogin1");
        testUser1.setEmail("test1@email.ru");
        testUser1.setBirthday(LocalDate.of(1984, 7, 15));

        List<User> userListBeforeAdd = userStorage.getAll();

        userStorage.add(testUser1);

        List<User> userListAfterAdd = userStorage.getAll();

        assertEquals(userListBeforeAdd.size() + 1, userListAfterAdd.size());
    }
}