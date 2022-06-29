package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class Film {
    private int id;

    private Set<Integer> idUsersWhoLiked = new HashSet<>();

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    private LocalDate releaseDate;

    @Positive
    private int duration;

    private FilmRate rate;

    private Set<FilmGenre> genres = new HashSet<>();

    public int likesQuantity() {
        return idUsersWhoLiked.size();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        values.put("rate", rate);
        return values;
    }
}
