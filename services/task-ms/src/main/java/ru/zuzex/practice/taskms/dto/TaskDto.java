package ru.zuzex.practice.taskms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.zuzex.practice.taskms.validation.OnCreate;
import ru.zuzex.practice.taskms.validation.OnUpdate;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {

    @NotNull(message = "TaskId must be not null", groups = OnUpdate.class)
    private UUID taskId;

    @NotNull(message = "AccountId must be not null", groups = {OnCreate.class, OnUpdate.class})
    private UUID accountId;

    @Length(min = 2, max = 100, message = "Title should be in range between 2 and 100 characters", groups = {OnCreate.class, OnUpdate.class})
    @NotNull(message = "Title must be not null", groups = {OnCreate.class, OnUpdate.class})
    private String title;

    @NotNull(message = "Body must be not null", groups = {OnCreate.class, OnUpdate.class})
    private String body;

    @NotNull(message = "Completed must be not null")
    private Boolean completed;
}
