package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class FilmService {

    private int idForFilms = 1;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        int id = idForFilms++;
        film.setId(id);

        if (filmValidation(film)) {
            filmStorage.addFilm(film);
            log.info("Added film name: {}, id: {}", film.getName(), film.getId());
        }
        return film;
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilm(id);
    }

    public Film updateFilm(Film film) {
        if (!filmStorage.filmExist(film)){
            log.warn("Unknown ID for update film!");
            throw new ValidationException("Unknown ID for update film!");
        }
        if (filmValidation(film)) {
            filmStorage.addFilm(film);
            log.info("Updated film id: {}", film.getId());
        }
        return film;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
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
