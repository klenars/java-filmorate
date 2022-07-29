package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                "FROM FILM " +
                "WHERE FILM_ID = ?";

        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
    }

    @Override
    public void update(Film film) {
        String sqlQuery = "UPDATE FILM " +
                "SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? " +
                "WHERE FILM_ID = ?";

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
        String sqlQuery = "DELETE FROM FILM WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        deleteGenre(film.getId());
        deleteDirectors(film.getId());
    }

    @Override
    public boolean isExistById(int id) {
        String sqlQuery = "SELECT FILM_ID " +
                "FROM FILM " +
                "WHERE FILM_ID = ?";

        return jdbcTemplate.queryForRowSet(sqlQuery, id).next();
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT * " +
                "FROM FILM";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public List<Film> getPopular(int count) {
        String query = "SELECT FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID, SCORE " +
                "FROM FILM " +
                "ORDER BY SCORE DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(query, this::mapRowToFilm, count);
    }

    @Override
    public List<Film> getPopularByGenre(int genreId, int count) {
        String sql = "SELECT F.FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID, SCORE " +
                "FROM FILM AS F " +
                "LEFT JOIN FILM_GENRE AS FG ON F.FILM_ID = FG.FILM_ID " +
                "WHERE FG.GENRE_ID = ? " +
                "ORDER BY SCORE DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, this::mapRowToFilm, genreId, count);
    }

    @Override
    public List<Film> getPopularByYear(int year, int count) {
        String sql = "SELECT FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID, SCORE " +
                "FROM FILM " +
                "WHERE EXTRACT(YEAR FROM RELEASE_DATE) = ? " +
                "ORDER BY SCORE DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, this::mapRowToFilm, year, count);
    }

    @Override
    public List<Film> getPopularByGenreAndYear(int genreId, int year, int count) {
        String sql = "SELECT F.FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID, SCORE " +
                "FROM FILM AS F " +
                "LEFT JOIN FILM_GENRE AS FG ON F.FILM_ID = FG.FILM_ID" +
                "WHERE GENRE_ID = ? AND EXTRACT(YEAR FROM RELEASE_DATE) = ? " +
                "ORDER BY SCORE DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, this::mapRowToFilm, genreId, year, count);
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        String sql = "SELECT F.FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID, SCORE " +
                "FROM FILM AS F" +
                "LEFT JOIN FILM_USER_LIKE AS FUL ON F.FILM_ID = FUL.FILM_ID " +
                "WHERE USER_ID IN (?, ?) " +
                "GROUP BY F.FILM_ID " +
                "HAVING COUNT(USER_ID) > 1";

        return jdbcTemplate.query(sql, this::mapRowToFilm, userId, friendId);
    }

    @Override
    public void deleteFilmById(int filmId) {
        String sqlQuery = "DELETE FROM FILM WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public List<Film> getFilmsLikeUser(int userId) {
        String sqlQuery = "SELECT F.FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID, F.SCORE " +
                "FROM FILM AS F " +
                "LEFT JOIN FILM_USER_LIKE AS FUL ON F.FILM_ID = FUL.FILM_ID " +
                "WHERE FUL.USER_ID = ? " +
                "GROUP BY F.FILM_ID";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, userId);
    }

    @Override
    public List<Film> getFilmBySubstringInDirector(String substring) {
        String sqlQuery = "SELECT F.FILM_ID, F.NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID, SCORE " +
                "FROM FILM AS F " +
                "LEFT JOIN FILM_DIRECTORS AS FD ON F.FILM_ID = FD.FILM_ID " +
                "LEFT JOIN DIRECTORS AS D ON FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                "WHERE LOWER(D.NAME) LIKE LOWER(?) " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY SCORE DESC";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, substring);

    }

    @Override
    public List<Film> getFilmBySubstringInTitle(String substring) {
        String sqlQuery = "SELECT FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID, SCORE " +
                "FROM FILM " +
                "WHERE LOWER(NAME) LIKE LOWER(?) " +
                "ORDER BY SCORE DESC";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, substring);
    }

    @Override
    public List<Film> getFilmBySubstringInDirectorAndTitle(String substring) {
        String sqlQuery = "SELECT F.FILM_ID, F.NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID, SCORE " +
                "FROM FILM AS F " +
                "LEFT JOIN FILM_DIRECTORS AS FD ON F.FILM_ID = FD.FILM_ID  " +
                "LEFT JOIN DIRECTORS AS D ON FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                "WHERE LOWER(d.NAME) LIKE LOWER(?) " +
                "AND LOWER(F.NAME) LIKE LOWER(?) " +
                "ORDER BY SCORE DESC";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, substring, substring);
    }

    @Override
    public List<Film> getDirectorFilmSortedByYearOrLikes(int directorId, String sort) {
        if (!directorStorage.isExistById(directorId)) {
            throw new ResourceNotFoundException(String.format("Director with id = %s doesn't exist!", directorId));
        }
        SqlRowSet sqlRowSet;
        if (sort.equals("likes")) {
            sqlRowSet = jdbcTemplate.queryForRowSet("SELECT FD.FILM_ID FROM DIRECTORS AS D " +
                    "LEFT JOIN FILM_DIRECTORS AS FD ON D.DIRECTOR_ID = FD.DIRECTOR_ID " +
                    "LEFT JOIN FILM AS F ON FD.FILM_ID = F.FILM_ID " +
                    "GROUP BY FD.FILM_ID, D.DIRECTOR_ID  " +
                    "HAVING D.DIRECTOR_ID = ? " +
                    "ORDER BY SCORE", directorId);
        } else {
            sqlRowSet = jdbcTemplate.queryForRowSet("SELECT FD.FILM_ID FROM DIRECTORS AS D " +
                    "LEFT JOIN FILM_DIRECTORS AS FD ON D.DIRECTOR_ID = FD.DIRECTOR_ID " +
                    "LEFT JOIN FILM AS F ON FD.FILM_ID = F.FILM_ID " +
                    "GROUP BY D.DIRECTOR_ID, FD.FILM_ID, EXTRACT (YEAR FROM F.RELEASE_DATE)" +
                    "HAVING D.DIRECTOR_ID = ?" +
                    "ORDER BY EXTRACT (YEAR FROM F.RELEASE_DATE)", directorId);
        }
        List<Integer> filmIds = new LinkedList<>();

        while (sqlRowSet.next()) {
            filmIds.add(sqlRowSet.getInt("film_id"));
        }
        String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));

        List<Film> filmList = jdbcTemplate.query(
                String.format("SELECT FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID, SCORE " +
                        "FROM FILM " +
                        "WHERE FILM_ID IN (%s)", inSql), this::mapRowToFilm, filmIds.toArray());
        Map<Integer, Film> filmMap = filmList.stream().collect(Collectors.toMap(Film::getId, k -> k));
        List<Film> films = new LinkedList<>();
        for (Integer filmId : filmIds) {
            films.add(filmMap.get(filmId));
        }
        return films;
    }

    @Override
    public List<Film> getRecommendations(int userId) {

        //получаем список id фильмов пользователя с отрицательными оценками
        SqlRowSet sqlIdFilmsNegative = jdbcTemplate.queryForRowSet("SELECT FILM_ID " +
                "FROM FILM_USER_LIKE " +
                "WHERE USER_ID = ? " +
                "AND SCORE <= 5", userId);
        List<String> idFilmsNegative = new ArrayList<>();
        while (sqlIdFilmsNegative.next()) {
            idFilmsNegative.add(String.valueOf(sqlIdFilmsNegative.getInt("film_id")));
        }

        //получаем список id фильмов пользователя с положительными оценками
        SqlRowSet sqlIdFilmsPositive = jdbcTemplate.queryForRowSet("SELECT FILM_ID " +
                "FROM FILM_USER_LIKE " +
                "WHERE USER_ID = ? " +
                "AND SCORE > 5", userId);
        List<String> idFilmsPositive = new ArrayList<>();
        while (sqlIdFilmsPositive.next()) {
            idFilmsPositive.add(String.valueOf(sqlIdFilmsPositive.getInt("film_id")));
        }

        String inSqlNegative = String.join(",", idFilmsNegative);
        String inSqlPositive = String.join(",", idFilmsPositive);

        //получаем кол-во фильмов у каждого пользователя с оценками похожими на оценки пользователя с id = userId
        SqlRowSet sqlCount = jdbcTemplate.queryForRowSet(
                String.format("SELECT COUNT(FILM_ID) " +
                        "FROM FILM_USER_LIKE " +
                        "WHERE FILM_ID IN (%s) AND SCORE > 5 " +
                        "OR FILM_ID IN (%s) AND SCORE <= 5 " +
                        "GROUP BY USER_ID " +
                        "HAVING USER_ID != ?", inSqlPositive, inSqlNegative), userId);

        //при разборе ответа на запрос сразу определяем максимальное из полученных значений -
        // максимальное кол-во фильмов, по которому есть аналогичные оценки у другого пользователя
        int maxCount = 0;
        while (sqlCount.next()) {
            int count = sqlCount.getInt("COUNT(FILM_ID)");
            if (maxCount < count) {
                maxCount = count;
            }
        }

        //получение списка id пользователей с максимальным пересечением по оценкам
        SqlRowSet sqlUserIds = jdbcTemplate.queryForRowSet(
                String.format("SELECT USER_ID " +
                        "FROM FILM_USER_LIKE " +
                        "WHERE FILM_ID IN (%s) AND SCORE > 5 " +
                        "OR FILM_ID IN (%s) AND SCORE <= 5 " +
                        "GROUP BY USER_ID " +
                        "HAVING COUNT(FILM_ID) = ?", inSqlPositive, inSqlNegative), maxCount);
        List<String> idUsers = new ArrayList<>();
        while (sqlUserIds.next()) {
            idUsers.add(String.valueOf(sqlUserIds.getInt("user_id")));
        }

        //получение списка фильмов пользователей с похожими оценками
        String inSqlUsers = String.join(",", idUsers);
        List<Film> films = jdbcTemplate.query(
                String.format("SELECT F.FILM_ID, F.NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID, F.SCORE " +
                        "FROM FILM AS F " +
                        "LEFT JOIN FILM_USER_LIKE AS FUL ON F.FILM_ID = FUL.FILM_ID " +
                        "WHERE FUL.USER_ID IN (%s) " +
                        "GROUP BY F.FILM_ID " +
                        "HAVING F.SCORE > 5", inSqlUsers), this::mapRowToFilm);

        //удаление из полученного списка фильмов, к которым пользователь уже поставил оценки
        films.removeAll(getFilmsLikeUser(userId));

        return films;
    }

    private void addGenreToFilm(int filmId, List<FilmGenre> genres) {
        deleteGenre(filmId);

        if (genres != null) {
            if (!genres.isEmpty()) {
                StringBuilder sqlQuery = new StringBuilder("INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES ");
                for (FilmGenre filmGenre : new HashSet<>(genres)) {
                    sqlQuery.append("(").append(filmId).append(", ").append(filmGenre.getId()).append("), ");
                }
                sqlQuery.delete(sqlQuery.length() - 2, sqlQuery.length());

                jdbcTemplate.update(sqlQuery.toString());
            }
        }
    }

    private void deleteGenre(int filmId) {
        String sqlQuery = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    private void addDirectorsToFilm(int filmId, List<Director> directors) {
        deleteDirectors(filmId);

        if (directors != null) {
            if (!directors.isEmpty()) {
                StringBuilder sqlQuery = new StringBuilder("INSERT INTO FILM_DIRECTORS (FILM_ID, DIRECTOR_ID) VALUES ");
                for (Director director : new HashSet<>(directors)) {
                    sqlQuery.append("(").append(filmId).append(", ").append(director.getId()).append("), ");
                }
                sqlQuery.delete(sqlQuery.length() - 2, sqlQuery.length());

                jdbcTemplate.update(sqlQuery.toString());
            }
        }
    }

    private void deleteDirectors(int filmId) {
        String sqlQuery = "DELETE FROM FILM_DIRECTORS WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    private Film mapRowToFilm(ResultSet resultSet, int i) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("film_id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setDuration(resultSet.getInt("duration"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setGenres(genreStorage.getFilmGenreList(film.getId()));
        film.setMpa(mpaStorage.getFilmRate(resultSet.getInt("mpa_id")));
        film.setDirectors(directorStorage.getFilmDirectorList(film.getId()));
        film.setScore(resultSet.getDouble("score"));
        return film;
    }
}