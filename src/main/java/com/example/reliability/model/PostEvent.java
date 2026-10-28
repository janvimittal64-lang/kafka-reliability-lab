package com.example.reliability.model;

import java.time.Instant;

public class PostEvent {
    private String eventId;
    private String content;
    private Instant scheduledAt;

    public PostEvent() {}
    public PostEvent(String eventId, String content, Instant scheduledAt) {
        this.eventId = eventId;
        this.content = content;
        this.scheduledAt = scheduledAt;
    }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Instant getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(Instant scheduledAt) { this.scheduledAt = scheduledAt; }
    @Override public String toString() {
        return "PostEvent{eventId='" + eventId + "', content='" + content + "', scheduledAt=" + scheduledAt + "}";
    }
}
