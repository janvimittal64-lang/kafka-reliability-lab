package com.example.reliability.service;

import com.example.reliability.model.PostEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
public class PostConsumer {
    private static final Logger log = LoggerFactory.getLogger(PostConsumer.class);
    private final IdempotencyService idempotencyService;

    public PostConsumer(IdempotencyService idempotencyService) {
        this.idempotencyService = idempotencyService;
    }

    @RetryableTopic(
        attempts = "4",
        backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 8000),
        dltTopicSuffix = ".DLT",
        autoCreateTopics = "true"
    )
    @KafkaListener(topics = "${app.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(PostEvent event) {
        String id = event.getEventId();
        if (idempotencyService.alreadyProcessed(id)) {
            log.info("DUPLICATE SKIPPED: eventId={}", id);
            return;
        }

        log.info("Processing eventId={}, content={}", id, event.getContent());

        // Demonstration failure switch: content containing [FAIL] always fails,
        // so you can observe retry attempts followed by delivery to the DLT.
        if (event.getContent() != null && event.getContent().contains("[FAIL]")) {
            throw new IllegalStateException("Intentional demo failure for event " + id);
        }

        // Mark only after successful processing, so failed attempts can be retried.
        idempotencyService.markProcessed(id);
        log.info("SUCCESS: eventId={}", id);
    }

    @DltHandler
    public void handleDeadLetter(PostEvent event) {
        log.error("DLQ RECEIVED: eventId={}, content={}", event.getEventId(), event.getContent());
    }
}
