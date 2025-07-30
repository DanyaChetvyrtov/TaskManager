package ru.zuzex.practice.profilems.model;

import lombok.Data;

import java.util.UUID;

@Data
public class Task {
    private UUID taskId;
    private UUID profileId;
    private String title;
    private String body;
    private Boolean completed;
}
