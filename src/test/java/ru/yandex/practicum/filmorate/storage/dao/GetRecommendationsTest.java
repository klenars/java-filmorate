package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmRate;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GetRecommendationsTest {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final LikeDao likeDao;

    @Test
    void getRecommendations() {
        Film film1 = new Film();
        FilmRate filmRate = new FilmRate();
        film1.setName("Film1");
        filmRate.setId(2);
        film1.setMpa(filmRate);
        film1.setReleaseDate(LocalDate.of(2020, 2, 22));
        filmDbStorage.add(film1);

        Film film2 = new Film();
        film2.setName("Film2");
        filmRate.setId(2);
        film2.setMpa(filmRate);
        film2.setReleaseDate(LocalDate.of(2020, 2, 22));
        filmDbStorage.add(film2);

        Film film3 = new Film();
        film3.setName("Film3");
        filmRate.setId(2);
        film3.setMpa(filmRate);
        film3.setReleaseDate(LocalDate.of(2020, 2, 22));
        filmDbStorage.add(film3);

        Film film4 = new Film();
        film4.setName("Film4");
        filmRate.setId(2);
        film4.setMpa(filmRate);
        film4.setReleaseDate(LocalDate.of(2020, 2, 22));
        filmDbStorage.add(film4);

        Film film5 = new Film();
        film5.setName("Film5");
        filmRate.setId(2);
        film5.setMpa(filmRate);
        film5.setReleaseDate(LocalDate.of(2020, 2, 22));
        filmDbStorage.add(film5);

        User testUser1 = new User();
        testUser1.setName("testName1");
        testUser1.setLogin("testLogin1");
        testUser1.setEmail("test1@email.ru");
        testUser1.setBirthday(LocalDate.of(1984, 7, 15));
        userDbStorage.add(testUser1);
        likeDao.addLike(film1.getId(), testUser1.getId(), 6);
        likeDao.addLike(film2.getId(), testUser1.getId(), 7);
        likeDao.addLike(film3.getId(), testUser1.getId(), 8);

        User testUser2 = new User();
        testUser2.setName("testName2");
        testUser2.setLogin("testLogin2");
        testUser2.setEmail("test2@email.ru");
        testUser2.setBirthday(LocalDate.of(1980, 8, 16));
        userDbStorage.add(testUser2);
        likeDao.addLike(film1.getId(), testUser2.getId(), 9);
        likeDao.addLike(film2.getId(), testUser2.getId(), 7);
        likeDao.addLike(film3.getId(), testUser2.getId(), 10);
        likeDao.addLike(film4.getId(), testUser2.getId(), 9);

        User testUser3 = new User();
        testUser3.setName("testName3");
        testUser3.setLogin("testLogin3");
        testUser3.setEmail("test3@email.ru");
        testUser3.setBirthday(LocalDate.of(2020, 2, 22));
        userDbStorage.add(testUser3);
        likeDao.addLike(film1.getId(), testUser3.getId(), 7);
        likeDao.addLike(film2.getId(), testUser3.getId(), 9);
        likeDao.addLike(film3.getId(), testUser3.getId(), 1);
        likeDao.addLike(film4.getId(), testUser3.getId(), 9);
        likeDao.addLike(film5.getId(), testUser3.getId(), 10);


        List<Film> recommendations = filmDbStorage.getRecommendations(testUser1.getId());

        assertEquals(1, recommendations.size());
        assertEquals(film4.getId(), recommendations.get(0).getId());
        assertEquals(film4.getName(), recommendations.get(0).getName());
    }
}