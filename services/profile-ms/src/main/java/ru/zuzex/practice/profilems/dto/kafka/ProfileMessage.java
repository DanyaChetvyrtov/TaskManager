package ru.zuzex.practice.profilems.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileMessage {
    private UUID id;
    private String eventType;
    private UUID profileId;
    private LocalDateTime timestamp;
}
