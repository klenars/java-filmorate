package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.time.Instant;
import java.util.List;

@Service
public class EventService {
    public final EventStorage eventStorage;

    public EventService(EventStorage eventStorage) {
        this.eventStorage = eventStorage;
    }

    public void addLikeEvent(int filmId, int userId) {
        Event event = getBaseEvent(userId, filmId);
        event.setEventType("LIKE");
        event.setOperation("ADD");

        eventStorage.add(event);
    }

    public void deleteLikeEvent(int filmId, int userId) {
        Event event = getBaseEvent(userId, filmId);
        event.setEventType("LIKE");
        event.setOperation("REMOVE");

        eventStorage.add(event);
    }

    public void addFriendEvent(int id, int friendId) {
        Event event = getBaseEvent(id, friendId);
        event.setEventType("FRIEND");
        event.setOperation("ADD");

        eventStorage.add(event);
    }

    public void deleteFriendEvent(int id, int friendId){
        Event event = getBaseEvent(id, friendId);
        event.setEventType("FRIEND");
        event.setOperation("REMOVE");

        eventStorage.add(event);
    }

    public void addReviewEvent() {
        //TODO
    }
    public void deleteReviewEvent() {
        //TODO
    }

    public List<Event> getFeed(int userId) {
        return eventStorage.getFeed(userId);
    }

    private Event getBaseEvent(int userId, int entityId) {
        Event event = new Event();
        event.setTimestamp(Instant.now().toEpochMilli());
        event.setUserId(userId);
        event.setEntityId(entityId);

        return event;
    }
}
