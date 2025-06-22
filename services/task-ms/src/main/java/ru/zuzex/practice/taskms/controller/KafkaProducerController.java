package ru.zuzex.practice.taskms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.zuzex.practice.taskms.service.TaskProducer;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kafka")
public class KafkaProducerController {
    private final TaskProducer taskProducer;

    @PostMapping("/tasks")
    public String createTask(@RequestBody String taskData) {
        taskProducer.sendTaskEvent(taskData);
        return "Task event sent to Kafka";
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody String message) {
        taskProducer.sendMessage(message);
        return ResponseEntity.ok("Message sent to Kafka topic");
    }
}
