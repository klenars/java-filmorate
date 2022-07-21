package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Event {
    private int eventId;
    private long timestamp;
    private int userId;
    private String eventType;
    private String operation;
    private int entityId;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("event_id", eventId);
        values.put("timestamp", timestamp);
        values.put("user_id", userId);
        values.put("event_type", eventType);
        values.put("operation", operation);
        values.put("entity_id", entityId);
        return values;
    }
}
