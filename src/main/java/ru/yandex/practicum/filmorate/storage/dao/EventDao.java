package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class EventDao implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    public EventDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void add(Event event) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("events")
                .usingGeneratedKeyColumns("event_id");
        event.setEventId(simpleJdbcInsert.executeAndReturnKey(event.toMap()).intValue());
    }

    @Override
    public List<Event> getFeed(int userId) {
        String sqlQuery = "SELECT * " +
                "FROM events " +
                "WHERE user_id = ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToEvent, userId);
    }

    private Event mapRowToEvent(ResultSet resultSet, int num) throws SQLException {
        Event event = new Event();
        event.setEventId(resultSet.getInt("event_id"));
        event.setTimestamp(resultSet.getLong("timestamp"));
        event.setUserId(resultSet.getInt("user_id"));
        event.setEventType(resultSet.getString("event_type"));
        event.setOperation(resultSet.getString("operation"));
        event.setEntityId(resultSet.getInt("entity_id"));

        return event;
    }
}
