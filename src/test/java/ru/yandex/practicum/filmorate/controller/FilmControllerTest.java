package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage()));

    @Test
    void addFilm() {
        Film film = new Film();
        film.setName("TestFilm");
        film.setDescription("TestDescription");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(90);
        assertThrows(ValidationException.class, () -> filmController.addFilm(film));

        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(-123);
        assertThrows(ValidationException.class, () -> filmController.addFilm(film));

        film.setDuration(60);
        film.setName("");
        assertThrows(ValidationException.class, () -> filmController.addFilm(film));

        film.setName("TestFilm");
        assertDoesNotThrow(() -> filmController.addFilm(film));

    }

    @Test
    void updateFilm() {
        Film film = new Film();
        film.setId(-1);

        assertThrows(ValidationException.class, () -> filmController.updateFilm(film));
    }
}