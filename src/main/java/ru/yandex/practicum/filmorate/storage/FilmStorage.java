package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    void add(Film film);

    void delete(Film film);

    Film get(int id);

    boolean isExist(int id);

    List<Film> getAll();
}
