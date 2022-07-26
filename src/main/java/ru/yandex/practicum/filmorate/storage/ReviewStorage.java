package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Set;

public interface ReviewStorage {
    Review addReview(Review review);

    Review updateReview(Review review);

    void deleteReview(int id);

    Review getReview(int id);

    Set<Review> getReviewsByFilmId(int filmId, int count);

    Review addLikeTheReview(int id, int userId);

    void deleteLikeTheReview(int id, int userId);

    Review addDislikeTheReview(int id, int userId);

    void deleteDislikeTheReview(int id, int userId);
}
