package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.dao.LikeDao;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final LikeStorage likeStorage;
    private final EventService eventService;

    @Autowired
    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            UserService userService,
            LikeDao likeStorage,
            EventService eventService
    ) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.likeStorage = likeStorage;
        this.eventService = eventService;
    }

    public Film add(Film film) {
        validation(film);
        filmStorage.add(film);
        log.info("Added film name: {}, id: {}", film.getName(), film.getId());
        return getById(film.getId());
    }

    public Film getById(int id) {
        isExists(id);
        return filmStorage.get(id);
    }

    public Film update(Film film) {
        isExists(film.getId());
        validation(film);
        filmStorage.update(film);
        log.info("Updated film id: {}", film.getId());
        Film updatedFilm = getById(film.getId());
        if (isNull(film.getDirectors()) | film.getDirectors().isEmpty()) {
            updatedFilm.setDirectors(null);
        }
        if (isNull(film.getGenres()) | film.getGenres().isEmpty()) {
            updatedFilm.setGenres(null);
        }
        return updatedFilm;
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public void addLike(int id, int userId) {
        isExists(id);
        userService.isExists(userId);

        likeStorage.addLike(id, userId);
        eventService.addLikeEvent(id, userId);
    }

    public void deleteLike(int id, int userId) {
        isExists(id);
        userService.isExists(userId);

        likeStorage.deleteLike(id, userId);
        eventService.deleteLikeEvent(id, userId);
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
        userService.isExists(userId);
        userService.isExists(friendId);

        return filmStorage.getCommonFilms(userId, friendId);
    }

    public void deleteFilmById(int filmId) {
        isExists(filmId);
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

    private void isExists(int id) {
        if (!filmStorage.isExist(id)) {
            log.warn(String.format("Film with id: %d doesn't exist!", id));
            throw new ResourceNotFoundException(String.format("Film with id: %d doesn't exist!", id));
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
            log.warn(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }
}
