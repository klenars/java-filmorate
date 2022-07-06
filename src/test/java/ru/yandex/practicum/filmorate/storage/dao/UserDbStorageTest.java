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

    static User testUser1;
    static User testUser2;
    static User testUser3;

    @BeforeAll
    static void createUsers() {
        testUser1 = new User();
        testUser1.setName("testName1");
        testUser1.setLogin("testLogin1");
        testUser1.setEmail("test1@email.ru");
        testUser1.setBirthday(LocalDate.of(1984, 7, 15));

        testUser2 = new User();
        testUser2.setName("testName2");
        testUser2.setLogin("testLogin2");
        testUser2.setEmail("test2@email.ru");
        testUser2.setBirthday(LocalDate.of(1980, 8, 16));

        testUser3 = new User();
        testUser3.setName("testName3");
        testUser3.setLogin("testLogin3");
        testUser3.setEmail("test3@email.ru");
        testUser3.setBirthday(LocalDate.of(2020, 2, 22));
    }

    @Test
    void add() {
        userStorage.add(testUser1);
        assertEquals(2, userStorage.getAll().size());
    }

    @Test
    void update() {
        userStorage.add(testUser2);
        testUser3.setId(2);
        userStorage.update(testUser3);

        User user = userStorage.get(2);
        assertThat(user).hasFieldOrPropertyWithValue("id", 2);
        assertThat(user).hasFieldOrPropertyWithValue("login", "testLogin3");
    }

    @Test
    void get() {
        User user = userStorage.get(3);
        assertThat(user).hasFieldOrPropertyWithValue("id", 3);
        assertThat(user).hasFieldOrPropertyWithValue("name", "testName1");
    }

    @Test
    void isExists() {
        assertFalse(userStorage.isExists(1));
    }

    @Test
    void delete() {
        userStorage.add(testUser1);
        userStorage.delete(testUser1);

        assertTrue(userStorage.getAll().isEmpty());
    }

    @Test
    void getAll() {
        List<User> userList = userStorage.getAll();
        assertEquals(0, userList.size());
    }
}