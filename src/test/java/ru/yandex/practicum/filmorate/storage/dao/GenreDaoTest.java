package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDaoTest {
    private final GenreDao genreDao;

    @Test
    void getById() {
        FilmGenre filmGenre = genreDao.getById(1);
        assertEquals("Комедия", filmGenre.getName());
    }

    @Test
    void getAll() {
        List<FilmGenre> filmGenreList = genreDao.getAll();
        assertEquals(6, filmGenreList.size());
    }
}