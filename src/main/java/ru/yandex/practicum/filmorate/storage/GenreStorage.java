package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.List;

public interface GenreStorage {

    FilmGenre getById(int id);

    List<FilmGenre> getAll();

    List<FilmGenre> getFilmGenreList(int filmId);
}
