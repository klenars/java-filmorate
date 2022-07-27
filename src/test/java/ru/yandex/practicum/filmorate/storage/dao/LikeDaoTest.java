package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmRate;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class LikeDaoTest {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final LikeDao likeDao;

    @Test
    void addLike() {
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
        filmDbStorage.add(filmToUpdate);

        User testUser1 = new User();
        testUser1.setName("testName1");
        testUser1.setLogin("testLogin1");
        testUser1.setEmail("test1@email.ru");
        testUser1.setBirthday(LocalDate.of(1984, 7, 15));
        userDbStorage.add(testUser1);

        likeDao.addLike(filmToUpdate.getId(), testUser1.getId(), score);

        assertEquals(filmToUpdate.getId(), filmDbStorage.getPopular(5).get(0).getId());
    }

    @Test
    void deleteLike() {
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
        filmDbStorage.add(filmToUpdate);

        User testUser1 = new User();
        testUser1.setName("testName1");
        testUser1.setLogin("testLogin1");
        testUser1.setEmail("test1@email.ru");
        testUser1.setBirthday(LocalDate.of(1984, 7, 15));
        userDbStorage.add(testUser1);

        int id = filmDbStorage.getPopular(5).get(0).getId();

        likeDao.addLike(filmToUpdate.getId(), testUser1.getId(), score);

        assertEquals(filmToUpdate.getId(), filmDbStorage.getPopular(5).get(0).getId());

        likeDao.deleteLike(filmToUpdate.getId(), testUser1.getId());

        assertEquals(id, filmDbStorage.getPopular(5).get(0).getId());
    }

    @Test
    void getPopular() {
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
        filmDbStorage.add(filmToUpdate);

        assertFalse(filmDbStorage.getPopular(10).isEmpty());
    }
}