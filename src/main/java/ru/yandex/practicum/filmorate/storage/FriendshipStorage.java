package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendshipStorage {

    void addFriend(int id, int friendId);

    void deleteFriend(int id, int friendId);

    List<User> getAllFriends(int id);

    List<User> getCommonFriends(int id, int otherId);
}
