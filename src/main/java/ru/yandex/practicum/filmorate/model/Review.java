package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Review implements Comparable<Review> {
    private int reviewId;
    private String content;
    @JsonProperty("isPositive")
    private Boolean isPositive;
    private int userId;
    private int filmId;
    private int useful;

    @Override
    public int compareTo(Review o) {
        if (this.useful == o.getUseful()) {
            return 1;
        } else {
            return o.useful - this.getUseful();
        }
    }
}
