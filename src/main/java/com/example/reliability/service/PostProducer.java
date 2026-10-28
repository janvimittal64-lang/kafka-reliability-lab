package com.example.reliability.service;

import com.example.reliability.model.PostEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PostProducer {
    private final KafkaTemplate<String, PostEvent> kafkaTemplate;
    private final String topic;

    public PostProducer(KafkaTemplate<String, PostEvent> kafkaTemplate,
                        @Value("${app.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(PostEvent event) {
        kafkaTemplate.send(topic, event.getEventId(), event);
    }
}
