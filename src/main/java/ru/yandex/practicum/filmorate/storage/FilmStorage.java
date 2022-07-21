package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    void add(Film film);

    void update(Film film);

    void delete(Film film);

    Film get(int id);

    boolean isExist(int id);

    List<Film> getAll();

    List<Film> getPopular(int count);

    List<Film> getPopularByGenre(int genreId, int count);

    List<Film> getPopularByYear(int year, int count);

    List<Film> getPopularByGenreAndYear(int genreId, int year, int count);

    void deleteFilmById(int filmId);

    List<Film> getFilmsLikeUser(int userId);

    List<Film> getCommonFilms(int userId, int friendId);

    List<Film> getFilmBySubstringInDirector(String substring);

    List<Film> getFilmBySubstringInTitle(String substring);

    List<Film> getFilmBySubstringInDirectorAndTitle(String substring);

    List<Film> getDirectorFilmSortedByYearOrLikes(int directorId, String sort);
}
