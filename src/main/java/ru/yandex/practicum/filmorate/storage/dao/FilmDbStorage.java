package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.FilmRate;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDao genreDao, MpaDao mpaDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
    }

    @Override
    public void add(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("film_id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue());

        if (film.getGenres() != null) {
            addGenreToFilm(film.getId(), film.getGenres());
        }
    }

    @Override
    public Film get(int id) {
        String sqlQuery = "SELECT * " +
                "FROM film " +
                "WHERE film_id = ?";

        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
    }

    @Override
    public void update(Film film) {
        String sqlQuery = "UPDATE film " +
                "SET name = ?, description = ?, release_date = ?, duration = ?, mpa = ? " +
                "WHERE film_id = ?";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        if (film.getGenres() != null) {
            addGenreToFilm(film.getId(), film.getGenres());
        }
    }

    @Override
    public void delete(Film film) {
        String sqlQuery = "DELETE FROM film WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        deleteGenre(film.getId());
    }

    @Override
    public boolean isExist(int id) {
        String sqlQuery = "SELECT film_id " +
                "FROM film " +
                "WHERE film_id = ?";

        return jdbcTemplate.queryForRowSet(sqlQuery, id).next();
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT * " +
                "FROM film";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    private void addGenreToFilm(int filmId, List<FilmGenre> genres) {
        deleteGenre(filmId);

        if (genres != null) {
            if (!genres.isEmpty()) {
                StringBuilder sqlQuery = new StringBuilder("INSERT INTO film_genre (film_id, genre_id) VALUES ");
                for (FilmGenre filmGenre : genres) {
                    sqlQuery.append("(").append(filmId).append(", ").append(filmGenre.getId()).append("), ");
                }
                sqlQuery.delete(sqlQuery.length() - 2, sqlQuery.length());

                jdbcTemplate.update(sqlQuery.toString());
            }
        }
    }

    private void deleteGenre(int filmId) {
        String sqlQuery = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    protected Film mapRowToFilm(ResultSet resultSet, int i) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("film_id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setDuration(resultSet.getInt("duration"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setGenres(getGenreList(film.getId()));
        film.setMpa(getFilmRate(resultSet.getInt("mpa")));

        return film;
    }

    private FilmRate getFilmRate(int mpaId) {
        String sqlQuery = "SELECT * " +
                "FROM mpa " +
                "WHERE mpa_id = ?";

        return jdbcTemplate.queryForObject(sqlQuery, mpaDao::mapRowToMpa, mpaId);
    }

    private List<FilmGenre> getGenreList(int filmId) {
        String sqlQuery = "SELECT * " +
                "FROM genre AS g " +
                "LEFT JOIN film_genre AS fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ?";

        List<FilmGenre> genres = jdbcTemplate.query(sqlQuery, genreDao::mapRowToGenre, filmId);
        if (genres.isEmpty()) {
            return null;
        } else {
            return genres;
        }
    }
}
