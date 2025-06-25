package ru.zuzex.practice.taskms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zuzex.practice.taskms.dto.kafka.AccountMessage;
import ru.zuzex.practice.taskms.repository.TaskRepository;

@Service
@RequiredArgsConstructor
public class AccountConsumer {
    private final ObjectMapper jacksonMapper;
    private final TaskRepository taskRepository;

    @SneakyThrows
    @Transactional
    @KafkaListener(topics = "tasks-topic", groupId = "account-group")
    public void listen(String message) {

        AccountMessage accountMessage = jacksonMapper.readValue(message, AccountMessage.class);
        System.out.println("Received Message: " + accountMessage);

        if (accountMessage.getEventType().equals("DeleteAccount"))
                taskRepository.deleteAllByAccountId(accountMessage.getAccountId());
    }
}
