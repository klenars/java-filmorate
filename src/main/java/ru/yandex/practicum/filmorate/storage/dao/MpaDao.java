package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.FilmRate;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MpaDao implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public FilmRate getById(int id) {
        if (!isExistById(id)) {
            String mes = String.format("Rate with id: %d doesn't exist!", id);
            throw new ResourceNotFoundException(mes);
        }

        String sqlQuery = "SELECT * " +
                "FROM MPA " +
                "WHERE MPA_ID = ?";

        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, id);
    }

    @Override
    public List<FilmRate> getAll() {
        String sqlQuery = "SELECT * " +
                "FROM MPA";

        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    @Override
    public FilmRate getFilmRate(int mpaId) {
        String sqlQuery = "SELECT * " +
                "FROM mpa " +
                "WHERE mpa_id = ?";

        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, mpaId);
    }

    private FilmRate mapRowToMpa(ResultSet resultSet, int i) throws SQLException {
        FilmRate filmRate = new FilmRate();
        filmRate.setId(resultSet.getInt("mpa_id"));
        filmRate.setName(resultSet.getString("name"));

        return filmRate;
    }

    private boolean isExistById(int id) {
        String sqlQuery = "SELECT mpa_id " +
                "FROM mpa " +
                "WHERE mpa_id = ?";

        return jdbcTemplate.queryForRowSet(sqlQuery, id).next();
    }
}
