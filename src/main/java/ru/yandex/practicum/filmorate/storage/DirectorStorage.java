package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {

    void add(Director director);                // Добавить режиссера

    void update(Director director);             // Обновить режиссера

    Director get(int id);                       // Получить режиссера по id

    List<Director> getAll();                    // Получить список всех режиссеров

    void deleteDirectorById(int userId);        // Удалить режиссера по id

    List<Director> getFilmDirectorList(int id); // Получить список режиссеров фильма по id фильма

    boolean isExist(int id);                    // Проверить существование в базе режиссера по id
}