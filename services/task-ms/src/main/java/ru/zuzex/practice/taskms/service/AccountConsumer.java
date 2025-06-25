package ru.zuzex.practice.taskms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zuzex.practice.taskms.dto.kafka.AccountMessage;
import ru.zuzex.practice.taskms.exception.exception.LocalAccountNotFoundException;
import ru.zuzex.practice.taskms.model.LocalAccount;
import ru.zuzex.practice.taskms.repository.LocalAccountRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountConsumer {
    private final ObjectMapper jacksonMapper;
    private final LocalAccountRepository localAccountRepository;

    @SneakyThrows
    @Transactional
    @KafkaListener(topics = "tasks-topic", groupId = "account-group")
    public void listen(String message) {

        AccountMessage accountMessage = jacksonMapper.readValue(message, AccountMessage.class);
        System.out.println("Received Message: " + accountMessage);

        if (accountMessage.getEventType().equals("DeleteAccount")) {
            var localAccount = localAccountRepository.findById(accountMessage.getAccountId())
                    .orElseThrow(LocalAccountNotFoundException::new);

            localAccount.setActive(false);
            localAccount.setDeletedAt(LocalDateTime.now());
            localAccountRepository.save(localAccount);
//            taskRepository.deleteAllByAccountId(accountMessage.getAccountId());
        } else if (accountMessage.getEventType().equals("CreateAccount")) {
            var newAccount = new LocalAccount();
            newAccount.setAccountId(accountMessage.getAccountId());
            newAccount.setActive(true);

            localAccountRepository.save(newAccount);
        } else System.out.println("Invalid event type: " + accountMessage.getEventType());
    }
}
