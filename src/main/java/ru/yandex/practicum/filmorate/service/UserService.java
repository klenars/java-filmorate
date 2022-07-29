package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;
    private final FilmStorage filmStorage;
    private final EventStorage eventStorage;

    public User add(User user) {
        validation(user);
        userStorage.add(user);
        log.info("Added user name: {}, id: {}", user.getName(), user.getId());
        return user;
    }

    public User getById(int id) {
        checkUserExist(id);
        return userStorage.get(id);
    }

    public User update(User user) {
        checkUserExist(user.getId());
        validation(user);
        userStorage.update(user);
        log.info("Updated user id: {}", user.getId());
        return getById(user.getId());
    }

    public List<User> getAll() {
        return new ArrayList<>(userStorage.getAll());
    }

    public void delete(User user) {
        checkUserExist(user.getId());
        userStorage.delete(user);
    }

    public void addFriend(int id, int friendId) {
        checkUserExist(id);
        checkUserExist(friendId);

        friendshipStorage.addFriend(id, friendId);
        eventStorage.addFriendEvent(id, friendId);
    }

    public void deleteFriend(int id, int friendId) {
        checkUserExist(id);
        checkUserExist(friendId);

        friendshipStorage.deleteFriend(id, friendId);
        eventStorage.deleteFriendEvent(id, friendId);
    }

    public List<User> getAllFriends(int id) {
        checkUserExist(id);

        return friendshipStorage.getAllFriends(id);
    }

    public List<User> getCommonFriends(int id, int otherId) {
        checkUserExist(id);
        checkUserExist(otherId);

        return friendshipStorage.getCommonFriends(id, otherId);
    }

    public void checkUserExist(int id) {
        if (!userStorage.isExistById(id)) {
            throw new ResourceNotFoundException(String.format("User with id: %d doesn't exist!", id));
        }
    }

    public void deleteUserById(int userId) {
        checkUserExist(userId);
        userStorage.deleteUserById(userId);
    }

    public List<Film> getRecommendations(int id) {
        checkUserExist(id);

        return filmStorage.getRecommendations(id);
    }

    public List<Event> getFeed(int userId) {
        checkUserExist(userId);

        return eventStorage.getFeed(userId);
    }

    private void validation(User user) {
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
            throw new ValidationException(errorMessage);
        }
    }
}
