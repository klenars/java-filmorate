package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Set;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review addReview(@RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review updateReview(@RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable int id) {
        reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable int id) {
        return reviewService.getReview(id);
    }

    @GetMapping
    public Set<Review> getReviewsByFilmId(@RequestParam(defaultValue = "0") String filmId,
                                          @RequestParam(defaultValue = "10") String count) {
        return reviewService.getReviewsByFilmId(Integer.parseInt(filmId), Integer.parseInt(count));
    }

    @PutMapping("/{id}/like/{userId}")
    public Review addLikeTheReview(@PathVariable int id, @PathVariable int userId) {
        return reviewService.addLikeTheReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeTheReview(@PathVariable int id, @PathVariable int userId) {
        reviewService.deleteLikeTheReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public Review addDislikeTheReview(@PathVariable int id, @PathVariable int userId) {
        return reviewService.addDislikeTheReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislikeTheReview(@PathVariable int id, @PathVariable int userId) {
        reviewService.deleteDislikeTheReview(id, userId);
    }
}
