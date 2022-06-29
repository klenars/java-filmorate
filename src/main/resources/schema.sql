CREATE TABLE IF NOT EXISTS "user"
(
    user_id  int PRIMARY KEY AUTO_INCREMENT,
    login    varchar,
    email    varchar,
    name     varchar,
    birthday date
);

CREATE TABLE IF NOT EXISTS film
(
    film_id      int PRIMARY KEY AUTO_INCREMENT,
    name         varchar,
    release_date date,
    duration     int,
    rate         varchar(11)
    );

CREATE TABLE IF NOT EXISTS genre
(
    genre_id int PRIMARY KEY AUTO_INCREMENT,
    name     varchar UNIQUE
);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_id  int,
    genre_id int,
    FOREIGN KEY (film_id) REFERENCES film (film_id),
    FOREIGN KEY (genre_id) REFERENCES genre (genre_id)
    );

CREATE TABLE IF NOT EXISTS user_friend
(
    user_id      int,
    friend_id    int,
    is_confirmed boolean,
    FOREIGN KEY (user_id) REFERENCES "user" (user_id),
    FOREIGN KEY (friend_id) REFERENCES "user" (user_id)
    );

CREATE TABLE IF NOT EXISTS film_user_like
(
    film_id int,
    user_id int,
    FOREIGN KEY (film_id) REFERENCES film (film_id),
    FOREIGN KEY (user_id) REFERENCES "user" (user_id)
    );