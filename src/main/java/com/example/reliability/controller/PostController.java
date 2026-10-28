package com.example.reliability.controller;

import com.example.reliability.model.PostEvent;
import com.example.reliability.service.PostProducer;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostProducer producer;
    public PostController(PostProducer producer) { this.producer = producer; }

    public record CreatePostRequest(@NotBlank String content, Instant scheduledAt, String eventId) {}

    @PostMapping
    public ResponseEntity<Map<String, String>> create(@RequestBody CreatePostRequest request) {
        String eventId = (request.eventId() == null || request.eventId().isBlank())
                ? UUID.randomUUID().toString() : request.eventId();
        Instant scheduledAt = request.scheduledAt() == null ? Instant.now() : request.scheduledAt();
        producer.publish(new PostEvent(eventId, request.content(), scheduledAt));
        return ResponseEntity.accepted().body(Map.of(
                "message", "Post event published to Kafka",
                "eventId", eventId,
                "topic", "scheduled-posts"
        ));
    }
}
