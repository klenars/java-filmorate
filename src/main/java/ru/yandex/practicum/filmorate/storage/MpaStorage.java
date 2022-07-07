package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FilmRate;

import java.util.List;

public interface MpaStorage {

    FilmRate getById(int id);

    List<FilmRate> getAll();

    FilmRate getFilmRate(int mpaId);
}
