package ru.zuzex.practice.taskms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Task data transfer object")
public class TaskDto {

    @Schema(description = "Task ID", example = "63ba3c9a-4c86-4a24-8e8a-5601edc55ef4", type = "UUID")
    @NotNull(message = "TaskId must be not null", groups = OnUpdate.class)
    private UUID taskId;

    @Schema(description = "Profile owner ID", example = "63ba3c9a-4c86-4a24-8e8a-5601edc55ef4", type = "UUID")
    @NotNull(message = "ProfileId must be not null", groups = {OnCreate.class, OnUpdate.class})
    private UUID profileId;

    @Schema(description = "Task title", example = "Add swagger to project", type = "String")
    @Length(min = 2, max = 100, message = "Title should be in range between 2 and 100 characters", groups = {OnCreate.class, OnUpdate.class})
    @NotNull(message = "Title must be not null", groups = {OnCreate.class, OnUpdate.class})
    private String title;

    @Schema(description = "Task content/body", example = "Add dependency. Basic configurations. Document Task model", type = "String")
    @NotNull(message = "Body must be not null", groups = {OnCreate.class, OnUpdate.class})
    private String body;

    @Schema(description = "Status", type = "Boolean")
    @NotNull(message = "Completed must be not null")
    private Boolean completed;
}
