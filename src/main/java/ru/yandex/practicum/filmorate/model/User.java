package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
public class User {
    private int id;

    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\S*$")
    private String login;

    private String name;

    @Past
    private LocalDate birthday;
}
