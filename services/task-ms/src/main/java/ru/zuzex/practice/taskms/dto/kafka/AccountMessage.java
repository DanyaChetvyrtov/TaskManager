package ru.zuzex.practice.taskms.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountMessage {
    private UUID id;
    private String eventType;
    private UUID accountId;
    private LocalDateTime timestamp;
}
