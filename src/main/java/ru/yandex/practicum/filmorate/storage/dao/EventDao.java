package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventDao implements EventStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Event> getFeed(int userId) {
        String sqlQuery = "SELECT * " +
                "FROM events " +
                "WHERE user_id = ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToEvent, userId);
    }

    @Override
    public void addLikeEvent(int filmId, int userId) {
        Event event = getBaseEvent(userId, filmId);
        event.setEventType(EventType.LIKE);
        event.setOperation(EventOperation.ADD);

        add(event);
    }

    @Override
    public void deleteLikeEvent(int filmId, int userId) {
        Event event = getBaseEvent(userId, filmId);
        event.setEventType(EventType.LIKE);
        event.setOperation(EventOperation.REMOVE);

        add(event);
    }

    @Override
    public void addFriendEvent(int id, int friendId) {
        Event event = getBaseEvent(id, friendId);
        event.setEventType(EventType.FRIEND);
        event.setOperation(EventOperation.ADD);

        add(event);
    }

    @Override
    public void deleteFriendEvent(int id, int friendId){
        Event event = getBaseEvent(id, friendId);
        event.setEventType(EventType.FRIEND);
        event.setOperation(EventOperation.REMOVE);

        add(event);
    }

    @Override
    public void addReviewEvent(Review review) {
        Event event = getBaseEvent(review.getUserId(), review.getReviewId());
        event.setEventType(EventType.REVIEW);
        event.setOperation(EventOperation.ADD);

        add(event);
    }

    @Override
    public void deleteReviewEvent(Review review) {
        Event event = getBaseEvent(review.getUserId(), review.getReviewId());
        event.setEventType(EventType.REVIEW);
        event.setOperation(EventOperation.REMOVE);

        add(event);
    }

    @Override
    public void updateReviewEvent(Review review) {
        Event event = getBaseEvent(review.getUserId(), review.getReviewId());
        event.setEventType(EventType.REVIEW);
        event.setOperation(EventOperation.UPDATE);

        add(event);
    }

    private void add(Event event) {
        String sql = "INSERT INTO EVENTS (TIMESTAMP, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(c -> {
            PreparedStatement ps = c.prepareStatement(sql, new String[]{"event_id"});
            ps.setLong(1, event.getTimestamp());
            ps.setInt(2, event.getUserId());
            ps.setString(3, event.getEventType().toString());
            ps.setString(4, event.getOperation().toString());
            ps.setInt(5, event.getEntityId());
            return ps;
        }, keyHolder);

        event.setEventId(keyHolder.getKey().intValue());
    }

    private Event getBaseEvent(int userId, int entityId) {
        Event event = new Event();
        event.setTimestamp(Instant.now().toEpochMilli());
        event.setUserId(userId);
        event.setEntityId(entityId);

        return event;
    }

    private Event mapRowToEvent(ResultSet resultSet, int num) throws SQLException {
        Event event = new Event();
        event.setEventId(resultSet.getInt("event_id"));
        event.setTimestamp(resultSet.getLong("timestamp"));
        event.setUserId(resultSet.getInt("user_id"));
        event.setEventType(EventType.valueOf(resultSet.getString("event_type")));
        event.setOperation(EventOperation.valueOf(resultSet.getString("operation")));
        event.setEntityId(resultSet.getInt("entity_id"));

        return event;
    }
}
