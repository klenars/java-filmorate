package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    void addUser(User user);

    User getUser(int id);

    boolean userIsExist(User user);

    List<User> getAllUsers();
}
