package ru.zuzex.practice.authms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zuzex.practice.authms.dto.kafka.AccountMessage;
import ru.zuzex.practice.authms.exception.exception.UserNotFoundException;
import ru.zuzex.practice.authms.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountConsumer {
    private final ObjectMapper jacksonMapper;
    private final UserRepository userRepository;

    @SneakyThrows
    @Transactional
    @KafkaListener(topics = "tasks-topic", groupId = "authentication-group")
    public void listen(String message) {

        AccountMessage accountMessage = jacksonMapper.readValue(message, AccountMessage.class);
        System.out.println("Received Message: " + accountMessage);

        if (accountMessage.getEventType().equals("DeleteAccount")) {
            var localAccount = userRepository.findById(accountMessage.getAccountId())
                    .orElseThrow(UserNotFoundException::new);

            localAccount.setIsActive(false);
            localAccount.setDeletedAt(LocalDateTime.now());
            userRepository.save(localAccount);
        }  else System.out.println("Invalid event type: " + accountMessage.getEventType());
    }
}
