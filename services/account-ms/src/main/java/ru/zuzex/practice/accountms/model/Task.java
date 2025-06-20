package ru.zuzex.practice.accountms.model;

import lombok.Data;

import java.util.UUID;

@Data
public class Task {
    private UUID taskId;
    private UUID accountId;
    private String title;
    private String body;
    private Boolean completed;
}
