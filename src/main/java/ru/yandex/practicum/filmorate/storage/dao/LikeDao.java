package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Repository
public class LikeDao {

    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmDbStorage;

    public LikeDao(JdbcTemplate jdbcTemplate, FilmDbStorage filmDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmDbStorage = filmDbStorage;
    }

    public void addLike(int id, int userId) {
        String sql = "INSERT INTO film_user_like (film_id, user_id) " +
                "VALUES (?, ?)";

        jdbcTemplate.update(sql, id, userId);
    }

    public void deleteLike(int id, int userId) {
        String sql = "DELETE FROM film_user_like " +
                "WHERE film_id = ? AND user_id = ?";

        jdbcTemplate.update(sql, id, userId);
    }

    public List<Film> getPopular(int count) {
        String sql = "SELECT * " +
                "FROM film AS f " +
                "LEFT JOIN film_user_like AS ful ON f.film_id = ful.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(ful.user_id) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, filmDbStorage::mapRowToFilm, count);
    }
}
