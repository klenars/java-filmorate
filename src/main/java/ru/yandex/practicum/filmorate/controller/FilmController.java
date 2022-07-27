package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

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
            @PathVariable int userId,
            @RequestParam
            @Min(value = 1, message = "Score is too low. Score must be from 1 to 10")
            @Max(value = 10, message = "Score is too high. Score must be from 1 to 10")
            int score
    ) {
        filmService.addLike(id, userId, score);
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
            @RequestParam int userId,
            @RequestParam int friendId) {
        return filmService.getCommonFilms(userId, friendId);
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
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}