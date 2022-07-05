package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.FilmRate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
public class MpaDao {
    private final JdbcTemplate jdbcTemplate;

    public MpaDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public FilmRate getById(int id) {
        if (!isExist(id)) {
            String mes = String.format("Genre with id: %d doesn't exist!", id);
            log.warn(mes);
            throw new ResourceNotFoundException(mes);
        }

        String sqlQuery = "SELECT * " +
                "FROM MPA " +
                "WHERE MPA_ID = ?";

        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, id);
    }

    public List<FilmRate> getAll() {
        String sqlQuery = "SELECT * " +
                "FROM MPA";

        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    protected FilmRate mapRowToMpa(ResultSet resultSet, int i) throws SQLException {
        FilmRate filmRate = new FilmRate();
        filmRate.setId(resultSet.getInt("mpa_id"));
        filmRate.setName(resultSet.getString("name"));

        return filmRate;
    }

    private boolean isExist(int id) {
        String sqlQuery = "SELECT GENRE_ID " +
                "FROM GENRE " +
                "WHERE GENRE_ID = ?";

        return jdbcTemplate.queryForRowSet(sqlQuery, id).next();
    }
}
