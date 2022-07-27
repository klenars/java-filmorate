package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        isExists(id);
        return userStorage.get(id);
    }

    public User update(User user) {
        isExists(user.getId());
        validation(user);
        userStorage.update(user);
        log.info("Updated user id: {}", user.getId());
        return getById(user.getId());
    }

    public List<User> getAll() {
        return new ArrayList<>(userStorage.getAll());
    }

    public void delete(User user) {
        isExists(user.getId());
        userStorage.delete(user);
    }

    public void addFriend(int id, int friendId) {
        isExists(id);
        isExists(friendId);

        friendshipStorage.addFriend(id, friendId);
        eventStorage.addFriendEvent(id, friendId);
    }

    public void deleteFriend(int id, int friendId) {
        isExists(id);
        isExists(friendId);

        friendshipStorage.deleteFriend(id, friendId);
        eventStorage.deleteFriendEvent(id, friendId);
    }

    public List<User> getAllFriends(int id) {
        isExists(id);

        return friendshipStorage.getAllFriends(id);
    }

    public List<User> getCommonFriends(int id, int otherId) {
        isExists(id);
        isExists(otherId);

        return friendshipStorage.getCommonFriends(id, otherId);
    }

    public void isExists(int id) {
        if (!userStorage.isExists(id)) {
            log.warn(String.format("User with id: %d doesn't exist!", id));
            throw new ResourceNotFoundException(String.format("User with id: %d doesn't exist!", id));
        }
    }

    public void deleteUserById(int userId) {
        isExists(userId);
        userStorage.deleteUserById(userId);
    }

    public List<Film> getRecommendations(int id) {
        isExists(id);

        User user = getById(id);
        List<Film> userListFilm = filmStorage.getFilmsLikeUser(id);

        List<User> allUsers = getAll();
        allUsers.remove(user);

        Map<User, List<Film>> userListMap = allUsers.stream()
                .collect(Collectors.toMap(Function.identity(), u -> filmStorage.getFilmsLikeUser(u.getId())));

        int maxFreq = 0;
        Map<User, Integer> sameUser = new HashMap<>();
        for (Map.Entry<User, List<Film>> entry : userListMap.entrySet()) {
            int freq = 0;
            for (Film film : entry.getValue()) {
                if (userListFilm.contains(film)) {
                    freq++;
                }
            }
            if (freq > maxFreq) {
                maxFreq = freq;
            }
            sameUser.put(entry.getKey(), freq);
        }

        List<Film> recommendation = new ArrayList<>();
        for (Map.Entry<User, Integer> userEntry : sameUser.entrySet()) {
            if (userListMap.get(userEntry.getKey()).size() > maxFreq) {
                List<Film> diff = userListMap.get(userEntry.getKey());
                diff.removeAll(userListFilm);

                recommendation.addAll(diff);
            }
        }

        return recommendation;
    }

    public List<Event> getFeed(int userId) {
        isExists(userId);

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
            log.warn(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }
}
