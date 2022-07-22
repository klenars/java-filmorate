package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface EventStorage {

    List<Event> getFeed(int userId);

    void addFriendEvent(int id, int friendId);

    void deleteFriendEvent(int id, int friendId);

    void addLikeEvent(int id, int userId);

    void deleteLikeEvent(int id, int userId);

    void addReviewEvent(Review reviewAnswer);

    void updateReviewEvent(Review reviewAnswer);

    void deleteReviewEvent(Review review);
}
