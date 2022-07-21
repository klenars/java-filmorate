package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Slf4j
@Repository
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review addReview(Review review) {
        checkUser(review.getUserId());
        checkFilm(review.getFilmId());
        checkPositiveNotNull(review.getIsPositive());
        String sqlQuery = "insert into REVIEWS (CONTENT, IS_POSITIVE, USER_ID, FILM_ID) values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"REVIEW_ID"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setInt(3, review.getUserId());
            stmt.setInt(4, review.getFilmId());
            return stmt;
        }, keyHolder);
        review.setReviewId(keyHolder.getKey().intValue());
        return getReview(review.getReviewId());
    }

    @Override
    public Review updateReview(Review review) {
        checkReview(review.getReviewId());
        String sqlQuery = "UPDATE REVIEWS " +
                "SET CONTENT = ?, IS_POSITIVE = ?" +
                "WHERE REVIEW_ID = ?";

        jdbcTemplate.update(sqlQuery,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
        return getReview(review.getReviewId());
    }

    @Override
    public void deleteReview(int id) {
        checkReview(id);
        String sqlQuery = "DELETE FROM REVIEWS " +
                "WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Review getReview(int id) {
        checkReview(id);
        String sqlQuery = "SELECT REVIEW_ID, CONTENT, IS_POSITIVE, USER_ID, FILM_ID " +
                "FROM REVIEWS " +
                "WHERE REVIEW_ID = ?";

        return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeReview(rs), id);
    }

    @Override
    public Set<Review> getReviewsByFilmId(int filmId, int count) {
        String sqlWhere = "";
        if (filmId != 0) {
            sqlWhere = "WHERE FILM_ID = ? ";
        }
        String sqlQuery = "SELECT REVIEW_ID, CONTENT, IS_POSITIVE, USER_ID, FILM_ID " +
                "FROM REVIEWS " +
                sqlWhere +
                "LIMIT ?";
        List<Review> reviews;
        if (filmId == 0) {
            reviews = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeReview(rs), count);
        } else {
            reviews = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeReview(rs), filmId, count);
        }
        Set<Review> sortReviews = new TreeSet<>(reviews);
        return sortReviews;
    }

    @Override
    public Review addLikeTheReview(int reviewId, int userId) {
        checkReview(reviewId);
        checkUser(userId);
        String sqlQuery = "INSERT INTO REVIEW_LIKES_DISLIKES (REVIEW_ID, USER_ID, IS_LIKE) VALUES(?, ?, ?)";
        jdbcTemplate.update(sqlQuery, reviewId, userId, true);

        return getReview(reviewId);
    }

    @Override
    public void deleteLikeTheReview(int reviewId, int userId) {
        checkReview(reviewId);
        checkUser(userId);
        String sqlQueryGenre = "DELETE FROM REVIEW_LIKES_DISLIKES WHERE REVIEW_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sqlQueryGenre, reviewId, userId);
    }

    @Override
    public Review addDislikeTheReview(int reviewId, int userId) {
        checkReview(reviewId);
        checkUser(userId);
        String sqlQuery = "INSERT INTO REVIEW_LIKES_DISLIKES (REVIEW_ID, USER_ID, IS_LIKE) VALUES(?, ?, ?)";
        jdbcTemplate.update(sqlQuery, reviewId, userId, false);
        return getReview(reviewId);
    }

    @Override
    public void deleteDislikeTheReview(int reviewId, int userId) {
        deleteLikeTheReview(reviewId, userId);
    }


    private void checkReview(int reviewId) {
        String sqlQuery = "SELECT * " +
                "FROM REVIEWS " +
                "WHERE REVIEW_ID = ?";
        if (!jdbcTemplate.queryForRowSet(sqlQuery, reviewId).next()) {
            log.warn(String.format("Review with id: %d doesn't exist!", reviewId));
            throw new ResourceNotFoundException(String.format("Review with id: %d doesn't exist!", reviewId));
        }
    }

    private void checkUser(int userId) {
        if (userId == 0) {
            log.warn(String.format("User with id: ", userId));
            throw new ValidationException(String.format("User with id: ", userId));
        }
        String sqlQuery = "SELECT * " +
                "FROM USERS " +
                "WHERE USER_ID = ?";
        if (!jdbcTemplate.queryForRowSet(sqlQuery, userId).next()) {
            log.warn(String.format("User with id: %d doesn't exist!", userId));
            throw new ResourceNotFoundException(String.format("User with id: %d doesn't exist!", userId));
        }
    }

    private void checkFilm(int filmId) {
        if (filmId == 0) {
            log.warn(String.format("Film with id: ", filmId));
            throw new ValidationException(String.format("Film with id: ", filmId));
        }
        String sqlQuery = "SELECT * " +
                "FROM FILM " +
                "WHERE FIlM_ID = ?";
        if (!jdbcTemplate.queryForRowSet(sqlQuery, filmId).next()) {
            log.warn(String.format("Film with id: %d doesn't exist!", filmId));
            throw new ResourceNotFoundException(String.format("Film with id: %d doesn't exist!", filmId));
        }
    }

    private void checkPositiveNotNull(Boolean isPositive) {
        if (isPositive == null) {
            log.warn(String.format("isPositive = null"));
            throw new ValidationException(String.format("isPositive = null"));
        }
    }

    private Review makeReview(ResultSet resultSet) throws SQLException {
        Review review = new Review();
        review.setReviewId(resultSet.getInt("REVIEW_ID"));
        review.setContent(resultSet.getString("CONTENT"));
        review.setIsPositive(resultSet.getBoolean("IS_POSITIVE"));
        review.setUserId(resultSet.getInt("USER_ID"));
        review.setFilmId(resultSet.getInt("FILM_ID"));
        review.setUseful(getUsefulOfReview(review.getReviewId()));

        return review;
    }

    private int getUsefulOfReview(int reviewId) {
        String sqlQueryLike = "SELECT COUNT(REVIEW_ID)" +
                " FROM REVIEW_LIKES_DISLIKES" +
                " WHERE IS_LIKE = TRUE AND REVIEW_ID = ?";
        String sqlQueryDislike = "SELECT COUNT(REVIEW_ID) " +
                "FROM REVIEW_LIKES_DISLIKES " +
                "WHERE IS_LIKE = FALSE AND REVIEW_ID = ?";
        int pointLikes = jdbcTemplate.queryForObject(sqlQueryLike, Integer.class, reviewId);
        int pointDislikes = jdbcTemplate.queryForObject(sqlQueryDislike, Integer.class, reviewId);
        return pointLikes - pointDislikes;
    }
}
