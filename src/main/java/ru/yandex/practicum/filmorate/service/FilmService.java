package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private static int idForFilms = 1;
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(
            FilmStorage filmStorage,
            UserService userService
    ) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film addFilm(Film film) {
        if (filmValidation(film)) {
            int id = idForFilms++;
            film.setId(id);
            filmStorage.addFilm(film);
            log.info("Added film name: {}, id: {}", film.getName(), film.getId());
        }
        return film;
    }

    public Film getFilmById(int id) {
        filmIsExists(id);

        return filmStorage.getFilm(id);
    }

    public Film updateFilm(Film film) {
        filmIsExists(film.getId());

        if (filmValidation(film)) {
            filmStorage.addFilm(film);
            log.info("Updated film id: {}", film.getId());
        }
        return film;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addLike(int id, int userId) {
        filmIsExists(id);

        filmStorage.getFilm(id).addLike(userId);
    }

    public void deleteLike(int id, int userId) {
        filmIsExists(id);
        userService.userIsExists(userId);

        filmStorage.getFilm(id).deleteLike(userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt(Film::likesQuantity).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void filmIsExists(int id) {
        if (!filmStorage.filmIsExist(id)) {
            log.warn(String.format("Film with id: %d doesn't exist!", id));
            throw new ResourceNotFoundException(String.format("Film with id: %d doesn't exist!", id));
        }
    }

    private boolean filmValidation(Film film) {
        String errorMessage = null;

        if (film.getName().isBlank()) {
            errorMessage = "Name is empty!";
        } else if (film.getDescription().length() > 200) {
            errorMessage = "Description is more than 200 characters!";
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            errorMessage = "Release date can't be earlier than 28-12-1895!";
        } else if (film.getDuration() < 0) {
            errorMessage = "Duration can't be negative!";
        }

        if (errorMessage != null) {
            log.warn(errorMessage);
            throw new ValidationException(errorMessage);
        } else {
            return true;
        }
    }
}
