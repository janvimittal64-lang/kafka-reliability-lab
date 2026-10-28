package com.example.reliability.service;

import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IdempotencyService {
    // Lab/demo only. Use a database table with a UNIQUE event_id constraint in production.
    private final Set<String> processedEventIds = ConcurrentHashMap.newKeySet();

    public boolean alreadyProcessed(String eventId) {
        return processedEventIds.contains(eventId);
    }

    public void markProcessed(String eventId) {
        processedEventIds.add(eventId);
    }
}
