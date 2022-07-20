package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.dao.LikeDao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final LikeStorage likeStorage;

    @Autowired
    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            UserService userService,
            LikeDao likeStorage
    ) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.likeStorage = likeStorage;
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
        return getById(film.getId());
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public void addLike(int id, int userId) {
        isExists(id);
        userService.isExists(userId);

        likeStorage.addLike(id, userId);
    }

    public void deleteLike(int id, int userId) {
        isExists(id);
        userService.isExists(userId);

        likeStorage.deleteLike(id, userId);
    }

    public List<Film> getPopular(int count, int genreId, int year) {
        List<Film> popular = filmStorage.getPopular(count);
        if (genreId == 0 && year == 0) {
            return popular;
        }
        Set<Film> mostPopular = new HashSet<>();
        for (Film film : popular) {
            for (FilmGenre genre : film.getGenres()) {
                if (genreId != 0 && year != 0) {
                    if (genre.getId() == genreId && film.getReleaseDate().getYear() == year) {
                        mostPopular.add(film);
                        break;
                    }
                } else if (genre.getId() == genreId || film.getReleaseDate().getYear() == year) {
                    mostPopular.add(film);
                    break;
                }
            }
        }
        return new ArrayList<>(mostPopular);
    }

    public void deleteFilmById(int filmId) {
        isExists(filmId);
        filmStorage.deleteFilmById(filmId);
    }

    private void isExists(int id) {
        if (!filmStorage.isExist(id)) {
            log.warn(String.format("Film with id: %d doesn't exist!", id));
            throw new ResourceNotFoundException(String.format("Film with id: %d doesn't exist!", id));
        }
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
