package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
public class GenreDao implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public FilmGenre getById(int id) {
        if (!isExist(id)) {
            String mes = String.format("Genre with id: %d doesn't exist!", id);
            log.warn(mes);
            throw new ResourceNotFoundException(mes);
        }

        String sqlQuery = "SELECT * " +
                "FROM genre " +
                "WHERE genre_id = ?";

        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
    }

    @Override
    public List<FilmGenre> getAll() {
        String sqlQuery = "SELECT * " +
                "FROM genre " +
                "ORDER BY 1";

        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public List<FilmGenre> getFilmGenreList(int filmId) {
        String sqlQuery = "SELECT * " +
                "FROM genre AS g " +
                "LEFT JOIN film_genre AS fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ? " +
                "ORDER BY g.genre_id";

        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId);
    }

    private FilmGenre mapRowToGenre(ResultSet resultSet, int i) throws SQLException {
        FilmGenre filmGenre = new FilmGenre();
        filmGenre.setId(resultSet.getInt("genre_id"));
        filmGenre.setName(resultSet.getString("name"));

        return filmGenre;
    }

    private boolean isExist(int id) {
        String sqlQuery = "SELECT GENRE_ID " +
                "FROM GENRE " +
                "WHERE GENRE_ID = ?";

        return jdbcTemplate.queryForRowSet(sqlQuery, id).next();
    }
}
