package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Data
public class Film {
    private int id;
    private Set<Integer> idUsersWhoLiked;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    private LocalDate releaseDate;

    @Positive
    private int duration;

    public void addLike(int userId) {
        idUsersWhoLiked.add(userId);
    }

    public void deleteLike(int userId) {
        idUsersWhoLiked.remove(userId);
    }

    public int likesQuantity() {
        return idUsersWhoLiked.size();
    }
}
