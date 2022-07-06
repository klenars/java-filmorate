package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmRate;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;

    @Test
    void add() {
        Film film = new Film();
        film.setName("FilmAdd");
        film.setDescription("FilmDescription");
        film.setReleaseDate(LocalDate.of(2020, 2, 22));
        film.setDuration(120);
        FilmRate filmRate = new FilmRate();
        filmRate.setId(1);
        film.setMpa(filmRate);

        filmDbStorage.add(film);

        assertThat(filmDbStorage.get(film.getId())).hasFieldOrPropertyWithValue("name", "FilmAdd");
    }

    @Test
    void get() {
        Film film = new Film();
        FilmRate filmRate = new FilmRate();
        film.setName("FilmGet");
        filmRate.setId(2);
        film.setMpa(filmRate);
        film.setReleaseDate(LocalDate.of(2020, 2, 22));

        filmDbStorage.add(film);

        assertThat(filmDbStorage.get(film.getId())).hasFieldOrPropertyWithValue("name", "FilmGet");
    }

    @Test
    void update() {
        Film film = new Film();
        FilmRate filmRate = new FilmRate();
        film.setName("Film");
        filmRate.setId(3);
        film.setMpa(filmRate);
        film.setReleaseDate(LocalDate.of(2020, 2, 22));

        filmDbStorage.add(film);

        Film filmToUpdate = new Film();
        filmToUpdate.setId(film.getId());
        FilmRate filmRate2 = new FilmRate();
        filmToUpdate.setName("FilmUpdated");
        filmRate2.setId(3);
        filmToUpdate.setMpa(filmRate2);
        filmToUpdate.setReleaseDate(LocalDate.of(2020, 2, 22));

        filmDbStorage.update(filmToUpdate);

        assertThat(filmDbStorage.get(film.getId())).hasFieldOrPropertyWithValue("name", "FilmUpdated");
    }

    @Test
    void delete() {
        Film film = new Film();
        FilmRate filmRate = new FilmRate();
        film.setName("Film");
        filmRate.setId(3);
        film.setMpa(filmRate);
        film.setReleaseDate(LocalDate.of(2020, 2, 22));

        filmDbStorage.add(film);

        assertTrue(filmDbStorage.isExist(film.getId()));

        filmDbStorage.delete(film);

        assertFalse(filmDbStorage.isExist(film.getId()));
    }

    @Test
    void isExist() {
        Film film = new Film();
        FilmRate filmRate = new FilmRate();
        film.setName("Film");
        filmRate.setId(3);
        film.setMpa(filmRate);
        film.setReleaseDate(LocalDate.of(2020, 2, 22));

        filmDbStorage.add(film);

        assertTrue(filmDbStorage.isExist(film.getId()));
    }

    @Test
    void getAll() {
        Film film = new Film();
        FilmRate filmRate = new FilmRate();
        film.setName("Film");
        filmRate.setId(3);
        film.setMpa(filmRate);
        film.setReleaseDate(LocalDate.of(2020, 2, 22));

        filmDbStorage.add(film);

        assertFalse(filmDbStorage.getAll().isEmpty());
    }
}