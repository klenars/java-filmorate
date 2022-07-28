package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {

    void add(Director director);

    void update(Director director);

    Director get(int id);

    List<Director> getAll();

    void deleteDirectorById(int userId);

    List<Director> getFilmDirectorList(int id);

    boolean isExistById(int id);
}