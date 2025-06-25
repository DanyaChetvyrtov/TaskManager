package ru.zuzex.practice.accountms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountProducer {
    private static final String TOPIC = "tasks-topic";
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String message) {
        kafkaTemplate.send(TOPIC, message);
        System.out.println("Produced message: " + message);
    }
}
