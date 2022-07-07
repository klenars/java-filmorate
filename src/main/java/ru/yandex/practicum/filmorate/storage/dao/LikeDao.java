package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

@Repository
@RequiredArgsConstructor
public class LikeDao implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(int id, int userId) {
        String sql = "INSERT INTO film_user_like (film_id, user_id) " +
                "VALUES (?, ?)";

        jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public void deleteLike(int id, int userId) {
        String sql = "DELETE FROM film_user_like " +
                "WHERE film_id = ? AND user_id = ?";

        jdbcTemplate.update(sql, id, userId);
    }
}
