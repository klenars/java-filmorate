package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.FilmRate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDaoTest {
    private final MpaDao mpaDao;

    @Test
    void getById() {
        FilmRate filmRate = mpaDao.getById(1);
        assertEquals("G", filmRate.getName());
    }

    @Test
    void getAll() {
        List<FilmRate> filmRateList = mpaDao.getAll();
        assertEquals(5, filmRateList.size());
    }
}