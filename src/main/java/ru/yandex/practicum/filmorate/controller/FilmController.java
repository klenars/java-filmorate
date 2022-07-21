package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable int id) {
        return filmService.getById(id);
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        return filmService.add(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @GetMapping
    public List<Film> getAll() {
        return filmService.getAll();
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(
            @PathVariable int id,
            @PathVariable int userId
    ) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(
            @PathVariable int id,
            @PathVariable int userId
    ) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") int count,
                                 @RequestParam(defaultValue = "0") int genreId,
                                 @RequestParam(defaultValue = "0") int year) {
        return filmService.getPopular(count, genreId, year);
    }

    @DeleteMapping("/{filmId}")
    public void deleteUserById(@PathVariable int filmId) {
        filmService.deleteFilmById(filmId);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(
            @RequestParam String userId,
            @RequestParam String friendId) {
        return filmService.getCommonFilms(Integer.parseInt(userId), Integer.parseInt(friendId));
    }

    @GetMapping("/search")
    public List<Film> getFilmBySubstring(
            @RequestParam(name = "query") String query,
            @RequestParam(name = "by") String by) {
        return filmService.getFilmBySubstring(query, by);
    }


    @GetMapping("/director/{directorId}")
    public List<Film> getDirectorFilmSortedByYearOrLikes(
            @PathVariable int directorId,
            @RequestParam String sortBy) {
        if (sortBy.equalsIgnoreCase("year") | sortBy.equalsIgnoreCase("likes")) {
            return filmService.getDirectorFilmSortedByYearOrLikes(directorId, sortBy);
        } else {
            throw new ValidationException("Incorrect sorting parameter!");
        }
    }
}

//TODO GET /films/director/:directorId?sortBy=[year, likes] список фильмов режиссера отсортированных по году выхода, лайкам