package ru.yandex.practicum.filmorate.storage;

public interface LikeStorage {

    void addLike(int id, int userId, int score);

    void deleteLike(int id, int userId);
}
