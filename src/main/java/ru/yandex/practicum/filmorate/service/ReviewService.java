package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final EventStorage eventStorage;

    public Review addReview(Review review) {
        Review reviewAnswer = reviewStorage.addReview(review);
        eventStorage.addReviewEvent(reviewAnswer);
        return reviewAnswer;
    }

    public Review updateReview(Review review) {
        Review reviewAnswer = reviewStorage.updateReview(review);
        eventStorage.updateReviewEvent(reviewAnswer);
        return reviewAnswer;
    }

    public void deleteReview(int id) {
        Review review = getReview(id);
        reviewStorage.deleteReview(id);
        eventStorage.deleteReviewEvent(review);
    }

    public Review getReview(int id) {
        return reviewStorage.getReview(id);
    }

    public Set<Review> getReviewsByFilmId(int filmId, int count) {
        return reviewStorage.getReviewsByFilmId(filmId, count);
    }

    public Review addLikeTheReview(int id, int userId) {
        return reviewStorage.addLikeTheReview(id, userId);
    }

    public void deleteLikeTheReview(int id, int userId) {
        reviewStorage.deleteLikeTheReview(id, userId);
    }

    public Review addDislikeTheReview(int id, int userId) {
        return reviewStorage.addDislikeTheReview(id, userId);
    }

    public void deleteDislikeTheReview(int id, int userId) {
        reviewStorage.deleteDislikeTheReview(id, userId);
    }
}
