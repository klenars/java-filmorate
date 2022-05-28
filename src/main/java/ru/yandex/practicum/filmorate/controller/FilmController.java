package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    Map<Integer, Film> films = new HashMap<>();

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        int id = film.getId();

        if (films.containsKey(id)){
            throw new ValidationException("ID already exist!");
        }
        if (filmValidation(film)) {
            films.put(id, film);
            return film;
        }
        return null;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (filmValidation(film)) {
            films.put(film.getId(), film);
            return film;
        }
        return null;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    private boolean filmValidation(Film film) {
        if (film.getName().isBlank()) {
            throw new ValidationException("Name is empty!");
        } else if (film.getDescription().length() > 200) {
            throw new ValidationException("Description is more than 200 characters!");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Release date can't be earlier than 28-12-1895!");
        } else if (film.getDuration().isNegative()) {
            throw new ValidationException("Duration can't be negative!");
        } else {
            return true;
        }
    }
}
