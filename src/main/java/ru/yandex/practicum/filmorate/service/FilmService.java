package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.LikeDao;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final LikeDao likeDao;

    @Autowired
    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            UserService userService,
            LikeDao likeDao
    ) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.likeDao = likeDao;
    }

    public Film add(Film film) {
        validation(film);
        filmStorage.add(film);
        log.info("Added film name: {}, id: {}", film.getName(), film.getId());
        return film;
    }

    public Film getById(int id) {
        isExists(id);
        return filmStorage.get(id);
    }

    public Film update(Film film) {
        isExists(film.getId());
        validation(film);
        filmStorage.update(film);
        log.info("Updated film id: {}", film.getId());
        return getById(film.getId());
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public void addLike(int id, int userId) {
        isExists(id);

        filmStorage.get(id).getIdUsersWhoLiked().add(userId);
    }

    public void deleteLike(int id, int userId) {
        isExists(id);
        userService.isExists(userId);

        filmStorage.get(id).getIdUsersWhoLiked().remove(userId);
    }

    public List<Film> getPopular(int count) {
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparingInt(Film::likesQuantity).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void isExists(int id) {
        if (!filmStorage.isExist(id)) {
            log.warn(String.format("Film with id: %d doesn't exist!", id));
            throw new ResourceNotFoundException(String.format("Film with id: %d doesn't exist!", id));
        }
    }

    private boolean validation(Film film) {
        String errorMessage = null;

        if (film.getName().isBlank()) {
            errorMessage = "Name is empty!";
        } else if (film.getDescription().length() > 200) {
            errorMessage = "Description is more than 200 characters!";
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            errorMessage = "Release date can't be earlier than 28-12-1895!";
        } else if (film.getDuration() < 0) {
            errorMessage = "Duration can't be negative!";
        } else if (film.getMpa() == null) {
            errorMessage = "MPA can't be NULL!";
        }

        if (errorMessage != null) {
            log.warn(errorMessage);
            throw new ValidationException(errorMessage);
        } else {
            return true;
        }
    }
}
