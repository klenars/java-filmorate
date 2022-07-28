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

CREATE TABLE IF NOT EXISTS REVIEWS
(
    REVIEW_ID   INT PRIMARY KEY AUTO_INCREMENT,
    CONTENT     VARCHAR NOT NULL,
    IS_POSITIVE BOOLEAN NOT NULL,
    USER_ID     INT REFERENCES USERS (USER_ID),
    FILM_ID     INT REFERENCES FILM (FILM_ID),
    CONSTRAINT "FK_REVIEWS_FILM" FOREIGN KEY (FILM_ID)
        REFERENCES film (FILM_ID)
        ON DELETE CASCADE,
    CONSTRAINT "FK_REVIEWS_USER" FOREIGN KEY (USER_ID)
        REFERENCES users (USER_ID)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS REVIEW_LIKES_DISLIKES
(
    REVIEW_ID INT,
    USER_ID   INT,
    IS_LIKE   BOOLEAN,
    CONSTRAINT "FK_REVIEWS_LIKES" FOREIGN KEY (REVIEW_ID)
        REFERENCES REVIEWS (REVIEW_ID)
        ON DELETE CASCADE,
    CONSTRAINT "FK_USERS_LIKES" FOREIGN KEY (USER_ID)
        REFERENCES USERS (USER_ID)
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
    score int,
    UNIQUE (film_id, user_id),
    CONSTRAINT "FK_LIKE" FOREIGN KEY (FILM_ID)
        REFERENCES film (FILM_ID)
        ON DELETE CASCADE,
    CONSTRAINT "US_LIKE" FOREIGN KEY (user_id)
        REFERENCES users (user_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS directors
(
    director_id int PRIMARY KEY AUTO_INCREMENT,
    name        varchar UNIQUE
);

CREATE TABLE IF NOT EXISTS film_directors
(
    film_id     int,
    director_id int,
    UNIQUE (film_id, director_id),
    CONSTRAINT "FK_FILM" FOREIGN KEY (FILM_ID)
        REFERENCES FILM (FILM_ID)
        ON DELETE CASCADE,
    CONSTRAINT "FK_DIRECTOR" FOREIGN KEY (DIRECTOR_ID)
        REFERENCES DIRECTORS (DIRECTOR_ID)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS events
(
    event_id   int PRIMARY KEY AUTO_INCREMENT,
    timestamp  long,
    user_id    int,
    event_type varchar,
    operation  varchar,
    entity_id  int,
    CONSTRAINT fk_user FOREIGN KEY (user_id)
        REFERENCES users (user_id)
        ON DELETE CASCADE,
    CONSTRAINT ev_type CHECK (event_type IN ('LIKE', 'REVIEW', 'FRIEND')),
    CONSTRAINT operation_type CHECK (operation IN ('REMOVE', 'ADD', 'UPDATE'))
)