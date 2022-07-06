package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FriendshipDaoTest {
    private final FriendshipDao friendshipDao;
    private final UserDbStorage userDbStorage;

    @Test
    void addFriend() {
        User user1 = new User();
        user1.setName("User1");
        user1.setLogin("testLogin1");
        user1.setEmail("test1@email.ru");
        user1.setBirthday(LocalDate.of(1984, 7, 15));
        userDbStorage.add(user1);

        User user2 = new User();
        user2.setName("User2");
        user2.setLogin("testLogin2");
        user2.setEmail("test2@email.ru");
        user2.setBirthday(LocalDate.of(1980, 8, 16));
        userDbStorage.add(user2);

        assertTrue(friendshipDao.getAllFriends(user1.getId()).isEmpty());

        friendshipDao.addFriend(user1.getId(), user2.getId());

        assertFalse(friendshipDao.getAllFriends(user1.getId()).isEmpty());
    }

    @Test
    void deleteFriend() {
        User user1 = new User();
        user1.setName("User1");
        user1.setLogin("testLogin1");
        user1.setEmail("test1@email.ru");
        user1.setBirthday(LocalDate.of(1984, 7, 15));
        userDbStorage.add(user1);

        User user2 = new User();
        user2.setName("User2");
        user2.setLogin("testLogin2");
        user2.setEmail("test2@email.ru");
        user2.setBirthday(LocalDate.of(1980, 8, 16));
        userDbStorage.add(user2);

        assertTrue(friendshipDao.getAllFriends(user1.getId()).isEmpty());

        friendshipDao.addFriend(user1.getId(), user2.getId());

        assertFalse(friendshipDao.getAllFriends(user1.getId()).isEmpty());

        friendshipDao.deleteFriend(user1.getId(), user2.getId());

        assertTrue(friendshipDao.getAllFriends(user1.getId()).isEmpty());
    }

    @Test
    void getAllFriends() {
        User user1 = new User();
        user1.setName("User1");
        user1.setLogin("testLogin1");
        user1.setEmail("test1@email.ru");
        user1.setBirthday(LocalDate.of(1984, 7, 15));
        userDbStorage.add(user1);

        User user2 = new User();
        user2.setName("User2");
        user2.setLogin("testLogin2");
        user2.setEmail("test2@email.ru");
        user2.setBirthday(LocalDate.of(1980, 8, 16));
        userDbStorage.add(user2);

        assertTrue(friendshipDao.getAllFriends(user1.getId()).isEmpty());

        friendshipDao.addFriend(user1.getId(), user2.getId());

        assertEquals(1, friendshipDao.getAllFriends(user1.getId()).size());
    }

    @Test
    void getCommonFriends() {
        User user1 = new User();
        user1.setName("User1");
        user1.setLogin("testLogin1");
        user1.setEmail("test1@email.ru");
        user1.setBirthday(LocalDate.of(1984, 7, 15));
        userDbStorage.add(user1);

        User user2 = new User();
        user2.setName("User2");
        user2.setLogin("testLogin2");
        user2.setEmail("test2@email.ru");
        user2.setBirthday(LocalDate.of(1980, 8, 16));
        userDbStorage.add(user2);

        User user3 = new User();
        user3.setName("User3");
        user3.setLogin("testLogin2");
        user3.setEmail("test2@email.ru");
        user3.setBirthday(LocalDate.of(1980, 8, 16));
        userDbStorage.add(user3);

        assertTrue(friendshipDao.getCommonFriends(user1.getId(), user2.getId()).isEmpty());

        friendshipDao.addFriend(user1.getId(), user3.getId());
        friendshipDao.addFriend(user2.getId(), user3.getId());

        assertEquals(1, friendshipDao.getCommonFriends(user1.getId(), user2.getId()).size());
    }
}