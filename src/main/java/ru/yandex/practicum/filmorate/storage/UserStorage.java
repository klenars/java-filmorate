package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User add(User user);

    void delete(User user);

    User get(int id);

    boolean isExists(int id);

    List<User> getAll();
}
