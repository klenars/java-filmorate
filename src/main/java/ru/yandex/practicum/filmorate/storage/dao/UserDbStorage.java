package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(User user) {
        String sql = "INSERT INTO USERS (login, email, name, birthday) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(c -> {
            PreparedStatement ps = c.prepareStatement(sql, new String[]{"user_id"});
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().intValue());
    }

    @Override
    public void update(User user) {
        String sqlQuery = "UPDATE users " +
                "SET login = ?, email = ?, name = ?, birthday = ? " +
                "WHERE user_id = ?";

        jdbcTemplate.update(sqlQuery,
                user.getLogin(),
                user.getEmail(),
                user.getName(),
                user.getBirthday(),
                user.getId());
    }

    @Override
    public User get(int id) {
        String sqlQuery = "SELECT user_id, login, email, name, birthday " +
                "FROM users " +
                "WHERE user_id = ?";

        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
    }

    @Override
    public boolean isExistById(int id) {
        String sqlQuery = "SELECT user_id " +
                "FROM users " +
                "WHERE user_id = ?";

        return jdbcTemplate.queryForRowSet(sqlQuery, id).next();
    }

    @Override
    public void delete(User user) {
            String sqlQuery = "DELETE FROM users WHERE user_id = ?";
            jdbcTemplate.update(sqlQuery, user.getId());
    }

    @Override
    public List<User> getAll() {
        String sqlQuery = "SELECT * " +
                "FROM users";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }
    public void deleteUserById(int userId){
        String sqlQuery = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery, userId);
    }

    private User mapRowToUser(ResultSet resultSet, int i) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("user_id"));
        user.setName(resultSet.getString("name"));
        user.setLogin(resultSet.getString("login"));
        user.setEmail(resultSet.getString("email"));
        user.setBirthday(resultSet.getDate("birthday").toLocalDate());

        return user;
    }
}
