package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.Set;

@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage) {
        this.reviewStorage = reviewStorage;
    }

    public Review addReview(Review review) {
        return reviewStorage.addReview(review);
    }

    public Review updateReview(Review review) {
        return reviewStorage.updateReview(review);
    }

    public void deleteReview(int id) {
        reviewStorage.deleteReview(id);
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
