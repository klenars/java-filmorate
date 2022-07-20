CREATE TABLE IF NOT EXISTS users
(
    user_id  int PRIMARY KEY AUTO_INCREMENT,
    login    varchar,
    email    varchar,
    name     varchar,
    birthday date
);

CREATE TABLE IF NOT EXISTS mpa
(
    mpa_id int PRIMARY KEY AUTO_INCREMENT,
    name   varchar UNIQUE

);

CREATE TABLE IF NOT EXISTS film
(
    film_id      int PRIMARY KEY AUTO_INCREMENT,
    name         varchar,
    description  varchar,
    release_date date,
    duration     int,
    mpa_id       int,
    FOREIGN KEY (mpa_id) REFERENCES mpa (mpa_id)
);

CREATE TABLE IF NOT EXISTS genre
(
    genre_id int PRIMARY KEY AUTO_INCREMENT,
    name     varchar UNIQUE
);

CREATE TABLE IF NOT EXISTS directors
(
    director_id int PRIMARY KEY AUTO_INCREMENT,
    name   varchar UNIQUE

);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_id  int,
    genre_id int,
    FOREIGN KEY (genre_id) REFERENCES genre (genre_id),
    UNIQUE (film_id, genre_id),
    CONSTRAINT "FK_GENRE" FOREIGN KEY (FILM_ID)
        REFERENCES film (FILM_ID)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS film_directors
(
    film_id  int,
    director_id int,
    FOREIGN KEY (director_id) REFERENCES DIRECTORS (director_id),
    UNIQUE (film_id, director_id),
    CONSTRAINT "FK_DIRECTOR" FOREIGN KEY (FILM_ID)
    REFERENCES film (FILM_ID)
    ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS user_friend
(
    user_id   int,
    friend_id int,
    CONSTRAINT "u_friend" FOREIGN KEY (user_id)
        REFERENCES users (user_id)
        ON DELETE CASCADE,
    CONSTRAINT "f_friend" FOREIGN KEY (friend_id)
        REFERENCES users (user_id)
        ON DELETE CASCADE

);

CREATE TABLE IF NOT EXISTS film_user_like
(
    film_id int,
    user_id int,
    CONSTRAINT "FK_LIKE" FOREIGN KEY (FILM_ID)
        REFERENCES film (FILM_ID)
        ON DELETE CASCADE
);