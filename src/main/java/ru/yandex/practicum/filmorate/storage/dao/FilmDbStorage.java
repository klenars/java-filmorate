package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Score;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final DirectorStorage directorStorage;

    @Override
    public void add(Film film) {
        String sql = "INSERT INTO FILM (NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(c -> {
            PreparedStatement ps = c.prepareStatement(sql, new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().intValue());

        addGenreToFilm(film.getId(), film.getGenres());
        addDirectorsToFilm(film.getId(), film.getDirectors());
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
                "SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE film_id = ?";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        addGenreToFilm(film.getId(), film.getGenres());
        addDirectorsToFilm(film.getId(), film.getDirectors());
    }

    @Override
    public void delete(Film film) {
        String sqlQuery = "DELETE FROM film WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        deleteGenre(film.getId());
        deleteDirectors(film.getId());
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

    @Override
    public List<Film> getPopular(int count) {
        String query = "SELECT F.FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID, AVG(SCORE) " +
                "FROM FILM AS F " +
                "LEFT JOIN FILM_USER_LIKE AS FUL ON F.FILM_ID = FUL.FILM_ID " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY AVG(SCORE) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(query, this::mapRowToFilm, count);
    }

    @Override
    public List<Film> getPopularByGenre(int genreId, int count) {
        String sql = "SELECT f.film_id, name, description, release_date, duration, mpa_id, AVG(ful.score) " +
                "FROM film AS f " +
                "LEFT JOIN film_user_like AS ful ON f.film_id = ful.film_id " +
                "WHERE f.film_id in (SELECT film_id " +
                "FROM film_genre AS fg " +
                "WHERE genre_id = ?) " +
                "GROUP BY f.film_id " +
                "ORDER BY AVG(ful.score) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, this::mapRowToFilm, genreId, count);
    }

    @Override
    public List<Film> getPopularByYear(int year, int count) {
        String sql = "SELECT f.film_id, name, description, release_date, duration, mpa_id, AVG(ful.score) " +
                "FROM film AS f " +
                "LEFT JOIN film_user_like AS ful ON f.film_id = ful.film_id " +
                "WHERE EXTRACT(YEAR FROM release_date) = ? " +
                "GROUP BY f.film_id " +
                "ORDER BY AVG(ful.score) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, this::mapRowToFilm, year, count);
    }

    @Override
    public List<Film> getPopularByGenreAndYear(int genreId, int year, int count) {
        String sql = "SELECT f.film_id, name, description, release_date, duration, mpa_id, AVG(ful.score) " +
                "FROM film AS f " +
                "LEFT JOIN film_user_like AS ful ON f.film_id = ful.film_id " +
                "WHERE f.film_id in (SELECT film_id " +
                "FROM film_genre AS fg " +
                "WHERE genre_id = ?) AND EXTRACT(YEAR FROM release_date) = ? " +
                "GROUP BY f.film_id " +
                "ORDER BY AVG(ful.score) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, this::mapRowToFilm, genreId, year, count);
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        String sql = "SELECT * " +
                "FROM film " +
                "WHERE film_id IN (" +
                "SELECT film_id " +
                "FROM film_user_like " +
                "WHERE user_id IN (?, ?) " +
                "GROUP BY film_id " +
                "HAVING COUNT(user_id) > 1)";

        return jdbcTemplate.query(sql, this::mapRowToFilm, userId, friendId);
    }

    @Override
    public void deleteFilmById(int filmId) {
        String sqlQuery = "DELETE FROM film WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public List<Film> getFilmsLikeUser(int userId) {
        String sqlQuery = "SELECT F.FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID, AVG(ful.score) " +
                "FROM film AS f " +
                "LEFT JOIN film_user_like AS ful ON f.film_id = ful.film_id " +
                "WHERE ful.user_id = ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, userId);
    }

    @Override
    public List<Film> getFilmBySubstringInDirector(String substring) {
        String sqlQuery = "SELECT F.FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID, AVG(ful.score) " +
                "FROM FILM AS F " +
                "LEFT JOIN FILM_USER_LIKE AS FUL ON F.FILM_ID = FUL.FILM_ID " +
                "WHERE F.FILM_ID IN (" +
                "SELECT FILM_ID FROM FILM_DIRECTORS WHERE DIRECTOR_ID IN (" +
                "SELECT DIRECTOR_ID FROM DIRECTORS WHERE LOWER(NAME) LIKE LOWER(?)))" +
                "GROUP BY F.FILM_ID " +
                "ORDER BY AVG(ful.score) DESC";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, substring);

    }

    @Override
    public List<Film> getFilmBySubstringInTitle(String substring) {
        String sqlQuery = "SELECT F.FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID, AVG(ful.score) " +
                "FROM FILM AS F " +
                "LEFT JOIN FILM_USER_LIKE AS FUL ON F.FILM_ID = FUL.FILM_ID " +
                "WHERE F.FILM_ID IN (" +
                "SELECT FILM_ID FROM FILM WHERE LOWER(F.NAME) LIKE LOWER(?)) " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY AVG(ful.score) DESC";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, substring);
    }

    @Override
    public List<Film> getFilmBySubstringInDirectorAndTitle(String substring) {
        String sqlQuery = "SELECT F.FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID, AVG(ful.score) " +
                "FROM FILM AS F " +
                "LEFT JOIN FILM_USER_LIKE FUL ON F.FILM_ID = FUL.FILM_ID " +
                "WHERE F.FILM_ID  IN (" +
                "SELECT FILM_ID FROM FILM_DIRECTORS WHERE DIRECTOR_ID IN (" +
                "SELECT DIRECTOR_ID FROM DIRECTORS WHERE LOWER(NAME) LIKE LOWER(?)))" +
                "OR LOWER(F.NAME) LIKE LOWER(?)" +
                "GROUP BY F.FILM_ID " +
                "ORDER BY AVG(ful.score) DESC";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, substring, substring);
    }

    @Override
    public List<Film> getDirectorFilmSortedByYearOrLikes(int directorId, String sort) {
        if (!directorStorage.isExist(directorId)) {
            throw new ResourceNotFoundException(String.format("Director with id = %s doesn't exist!", directorId));
        }
        SqlRowSet sqlRowSet;
        try {
            if (sort.equals("likes")) {
                sqlRowSet = jdbcTemplate.queryForRowSet("select fd.film_id from directors d " +
                        "left join film_directors fd on d.director_id = fd.director_id " +
                        "left join film f on fd.film_id = f.film_id " +
                        "left join film_user_like ful on ful.film_id = f.film_id " +
                        "group by fd.film_id, d.director_id  " +
                        "having  d.director_id = ? " +
                        "order by avg(ful.user_id)", directorId);
            } else {
                sqlRowSet = jdbcTemplate.queryForRowSet("select fd.film_id from directors d " +
                        "join film_directors fd on d.director_id = fd.director_id " +
                        "join film f on fd.film_id = f.film_id " +
                        "group by d.director_id, fd.film_id, extract (year from f.release_date)" +
                        "having d.director_id = ?" +
                        "order by extract (year from f.release_date)", directorId);
            }
            List<Film> films = new LinkedList<>();
            while (sqlRowSet.next()) {
                Film film = get(sqlRowSet.getInt("film_id"));
                films.add(film);
            }
            return films;
        } catch (Throwable e) {
            throw new ValidationException(e.getMessage());
        }
    }

    @Override
    public Score getScore(int filmId) {
        String query = "SELECT AVG(SCORE) FROM FILM_USER_LIKE WHERE FILM_ID=? AND USER_ID IN (" +
                "SELECT DISTINCT USER_ID FROM FILM_USER_LIKE)";
        return jdbcTemplate.query(query,
                        this::mapRowToScore,
                        filmId)
                .stream()
                .findAny()
                .orElse(null);
    }

    private void addGenreToFilm(int filmId, List<FilmGenre> genres) {
        deleteGenre(filmId);

        if (genres != null) {
            if (!genres.isEmpty()) {
                StringBuilder sqlQuery = new StringBuilder("INSERT INTO film_genre (film_id, genre_id) VALUES ");
                for (FilmGenre filmGenre : new HashSet<>(genres)) {
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

    private void addDirectorsToFilm(int filmId, List<Director> directors) {
        deleteDirectors(filmId);

        if (directors != null) {
            if (!directors.isEmpty()) {
                StringBuilder sqlQuery = new StringBuilder("INSERT INTO film_directors (film_id, director_id) VALUES ");
                for (Director director : new HashSet<>(directors)) {
                    sqlQuery.append("(").append(filmId).append(", ").append(director.getId()).append("), ");
                }
                sqlQuery.delete(sqlQuery.length() - 2, sqlQuery.length());

                jdbcTemplate.update(sqlQuery.toString());
            }
        }
    }

    private void deleteDirectors(int filmId) {
        String sqlQuery = "DELETE FROM film_directors WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    protected Film mapRowToFilm(ResultSet resultSet, int i) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("film_id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setDuration(resultSet.getInt("duration"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setGenres(genreStorage.getFilmGenreList(film.getId()));
        film.setMpa(mpaStorage.getFilmRate(resultSet.getInt("mpa_id")));
        film.setDirectors(directorStorage.getFilmDirectorList(film.getId()));
        film.setScore(getScore(film.getId()));
        return film;
    }

    private Score mapRowToScore(ResultSet resultSet, int i) throws SQLException {
        Score score = new Score();
        score.setGrade(resultSet.getDouble(1));
        return score;
    }


}