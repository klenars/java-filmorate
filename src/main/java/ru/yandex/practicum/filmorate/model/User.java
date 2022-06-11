package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.Set;

@Data
public class User {
    private int id;
    private Set<Integer> friendsIDs;

    @Email
    private String email;

    @NotBlank
    private String login;

    private String name;

    @Past
    private LocalDate birthday;

    public void addFriend(int friendId) {
        friendsIDs.add(friendId);
    }

    public void deleteFriend(int friendId) {
        friendsIDs.remove(friendId);
    }
}
