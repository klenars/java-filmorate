package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Data
public class Film {
    private int id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    private LocalDate releaseDate;

    @Positive
    private int duration;

    private FilmRate mpa;

    private List<FilmGenre> genres = new ArrayList<>();

    private List<Director> directors = new ArrayList<>();

    private Double score;
}