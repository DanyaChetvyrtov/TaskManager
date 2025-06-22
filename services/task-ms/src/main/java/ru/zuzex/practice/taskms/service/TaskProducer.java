package ru.zuzex.practice.taskms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskProducer {
    private static final String TOPIC = "tasks-topic";
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendTaskEvent(String taskData) {
        kafkaTemplate.send(TOPIC, taskData);
    }

    public void sendMessage(String message) {
        kafkaTemplate.send(TOPIC, message);
        System.out.println("Produced message: " + message);
    }
}
