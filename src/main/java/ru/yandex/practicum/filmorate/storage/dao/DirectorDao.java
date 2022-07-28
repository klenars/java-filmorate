package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DirectorDao implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(Director director) {
        String sql = "INSERT INTO DIRECTORS (NAME) " +
                "VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(c -> {
            PreparedStatement ps = c.prepareStatement(sql, new String[]{"director_id"});
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);

        director.setId(keyHolder.getKey().intValue());
    }

    @Override
    public void update(Director director) {
        String sqlQuery = "UPDATE directors " +
                "SET name = ? " +
                "WHERE director_id = ?";

        jdbcTemplate.update(sqlQuery,
                director.getName(),
                director.getId());
    }

    @Override
    public Director get(int directorId) {
        if (!isExistById(directorId)) {
            String mes = String.format("Director with id: %d doesn't exist!", directorId);
            log.warn(mes);
            throw new ResourceNotFoundException(mes);
        }

        String sqlQuery = "SELECT * " +
                "FROM directors " +
                "WHERE director_id = ?";

        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToDirector, directorId);
    }

    @Override
    public List<Director> getAll() {
        String sqlQuery = "SELECT * " +
                "FROM directors " +
                "ORDER BY 1";

        return jdbcTemplate.query(sqlQuery, this::mapRowToDirector);
    }

    @Override
    public void deleteDirectorById(int directorId) {
        String sqlQuery = "DELETE FROM directors WHERE director_id = ?";
        jdbcTemplate.update(sqlQuery, directorId);
    }

    @Override
    public List<Director> getFilmDirectorList(int filmId) {

            String sqlQuery = "SELECT * " +
                    "FROM directors AS d " +
                    "LEFT JOIN film_directors AS fd ON d.director_id = fd.director_id " +
                    "WHERE fd.film_id = ? " +
                    "ORDER BY d.director_id";

            return jdbcTemplate.query(sqlQuery, this::mapRowToDirector, filmId);
    }

    private Director mapRowToDirector(ResultSet resultSet, int i) throws SQLException {
        Director director = new Director();
        director.setId(resultSet.getInt("director_id"));
        director.setName(resultSet.getString("name"));

        return director;
    }

    public boolean isExistById(int id) {
        String sqlQuery = "SELECT DIRECTOR_ID " +
                "FROM DIRECTORS " +
                "WHERE DIRECTOR_ID = ?";

        return jdbcTemplate.queryForRowSet(sqlQuery, id).next();
    }
}