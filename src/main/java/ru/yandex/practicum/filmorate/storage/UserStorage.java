package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    void add(User user);

    void update(User user);

    void delete(User user);

    User get(int id);

    boolean isExistById(int id);

    List<User> getAll();
    void deleteUserById(int userId);
}
