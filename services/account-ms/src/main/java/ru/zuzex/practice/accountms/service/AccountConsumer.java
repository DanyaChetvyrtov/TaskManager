package ru.zuzex.practice.accountms.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class AccountConsumer {
    @KafkaListener(topics = "tasks-topic", groupId = "account-group")
    public void listen(String message) {
        System.out.println("Received Message: " + message);
    }
}
