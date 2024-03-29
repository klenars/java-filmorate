package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;
    private final EventStorage eventStorage;

    public Film add(Film film) {
        validation(film);
        filmStorage.add(film);
        log.info("Added film name: {}, id: {}", film.getName(), film.getId());
        return getById(film.getId());
    }

    public Film getById(int id) {
        checkFilmExist(id);
        return filmStorage.get(id);
    }

    public Film update(Film film) {
        checkFilmExist(film.getId());
        validation(film);
        filmStorage.update(film);
        log.info("Updated film id: {}", film.getId());
        Film updatedFilm = getById(film.getId());
        if (isNull(film.getDirectors()) | film.getDirectors().isEmpty()) {
            updatedFilm.setDirectors(null);
        }
        return updatedFilm;
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public void addLike(int id, int userId, int score) {
        checkFilmExist(id);
        checkUserExist(userId);

        likeStorage.addLike(id, userId, score);
        eventStorage.addLikeEvent(id, userId);
    }

    public void deleteLike(int id, int userId) {
        checkFilmExist(id);
        checkUserExist(userId);

        likeStorage.deleteLike(id, userId);
        eventStorage.deleteLikeEvent(id, userId);
    }

    public List<Film> getPopular(int count, int genreId, int year) {
        if (genreId == 0 && year == 0) {
            return filmStorage.getPopular(count);
        } else if (genreId != 0 && year != 0) {
            return filmStorage.getPopularByGenreAndYear(genreId, year, count);
        } else if (genreId != 0) {
            return filmStorage.getPopularByGenre(genreId, count);
        } else {
            return filmStorage.getPopularByYear(year, count);
        }
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        checkUserExist(userId);
        checkUserExist(friendId);

        return filmStorage.getCommonFilms(userId, friendId);
    }

    public void deleteFilmById(int filmId) {
        checkFilmExist(filmId);
        filmStorage.deleteFilmById(filmId);
    }

    public List<Film> getFilmBySubstring(String substring, String by) {
        List<Film> films = new LinkedList<>();
        substring = "%" + substring + "%";
        if (by.equals("director")) {
            films = filmStorage.getFilmBySubstringInDirector(substring);
        }
        if (by.equals("title")) {
            films = filmStorage.getFilmBySubstringInTitle(substring);
        }
        if ((by.equals("director,title")) || (by.equals("title,director"))) {
            films = filmStorage.getFilmBySubstringInDirectorAndTitle(substring);
        }
        return films;
    }

    private void checkFilmExist(int id) {
        if (!filmStorage.isExistById(id)) {
            throw new ResourceNotFoundException(String.format("Film with id: %d doesn't exist!", id));
        }
    }

    private void checkUserExist(int userId) {
        if (!userStorage.isExistById(userId)) {
            throw new ResourceNotFoundException(String.format("User with id: %d doesn't exist!", userId));
        }
    }

    public List<Film> getDirectorFilmSortedByYearOrLikes(int directorId, String sort) {
        return filmStorage.getDirectorFilmSortedByYearOrLikes(directorId,sort);
    }

    private void validation(Film film) {
        String errorMessage = null;

        if (film.getName().isBlank()) {
            errorMessage = "Name is empty!";
        } else if (film.getDescription().length() > 200) {
            errorMessage = "Description is more than 200 characters!";
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            errorMessage = "Release date can't be earlier than 28-12-1895!";
        } else if (film.getDuration() < 0) {
            errorMessage = "Duration can't be negative!";
        } else if (film.getMpa() == null) {
            errorMessage = "MPA can't be NULL!";
        }

        if (errorMessage != null) {
            throw new ValidationException(errorMessage);
        }
    }
}
