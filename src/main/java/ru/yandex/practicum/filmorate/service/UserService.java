package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private int idForUsers = 1;
    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        int id = idForUsers++;
        user.setId(id);

        if (userValidation(user)) {
            userStorage.addUser(user);
            log.info("Added user name: {}, id: {}", user.getName(), user.getId());
        }
        return user;
    }

    public User getUserById(int id) {
        userIsExists(id);
        return userStorage.getUser(id);
    }

    public User updateUser(User user) {
        userIsExists(user.getId());
        if (userValidation(user)) {
            userStorage.addUser(user);
            log.info("Updated user id: {}", user.getId());
        }
        return user;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userStorage.getAllUsers());
    }

    public void addFriend(int id, int friendId) {
        userIsExists(id);
        userIsExists(friendId);

        userStorage.getUser(id).addFriend(friendId);
    }

    public void deleteFriend(int id, int friendId) {
        userIsExists(id);
        userIsExists(friendId);

        userStorage.getUser(id).deleteFriend(friendId);
    }

    public List<User> getAllFriends(int id) {
        userIsExists(id);

        return userStorage.getUser(id).getFriendsIDs().stream()
                .map(i -> userStorage.getUser(i))
                .collect(Collectors.toList());
    }

    private boolean userIsExists(int id) {
        if (!userStorage.userIsExist(id)){
            log.warn(String.format("User with id: %d doesn't exist!", id));
            throw new ValidationException(String.format("User with id: %d doesn't exist!", id));
        }
        return true;
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
