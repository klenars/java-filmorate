package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendshipDaoImpl implements FriendshipStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(int id, int friendId) {
        String sqlQuery = "INSERT INTO user_friend (user_id, friend_id)" +
                "VALUES (?, ?)";

        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        String sqlQuery = "DELETE FROM user_friend " +
                "WHERE user_id = ? AND friend_id = ?";

        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    @Override
    public List<User> getAllFriends(int id) {
        String sqlQuery = "SELECT * " +
                "FROM users AS u " +
                "LEFT JOIN user_friend AS uf ON u.user_id = uf.friend_id " +
                "WHERE uf.user_id = ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id);
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        String sqlQuery = "SELECT * " +
                "FROM users " +
                "WHERE USER_ID IN (" +
                                "SELECT friend_id " +
                                "FROM user_friend " +
                                "WHERE user_id IN (?, ?) " +
                                "GROUP BY friend_id " +
                                "HAVING COUNT(friend_id) > 1)";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id, otherId);
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
