package ru.zuzex.practice.taskms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.zuzex.practice.taskms.dto.TaskDto;
import ru.zuzex.practice.taskms.mapper.TaskMapper;
import ru.zuzex.practice.taskms.service.TaskService;
import ru.zuzex.practice.taskms.validation.OnCreate;
import ru.zuzex.practice.taskms.validation.OnUpdate;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(name = "Profile API", description = "Task endpoints")
@RestController
@RequestMapping("api/v1/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final TaskMapper taskMapper;


    @GetMapping
    @PreAuthorize("@customSecurityExpression.hasPermissionTaskS_(#profileId)")
    @Operation(
            summary = "Receive all profile tasks by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks"),
                    @ApiResponse(responseCode = "404", description = "Tasks not found")
            }
    )
    public ResponseEntity<List<TaskDto>> getTasksByProfileId(@RequestParam(name = "profileId") UUID profileId) {
        var tasks = taskService.getAllByProfileId(profileId)
                .stream().map(taskMapper::toDto).toList();

        return ResponseEntity.ok().body(tasks);
    }

    @GetMapping("/{taskId}")
    @PreAuthorize("@customSecurityExpression.hasPermissionTask(#taskId)")
    @Operation(
            summary = "Receive a specific task by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved task"),
                    @ApiResponse(responseCode = "404", description = "Task not found")
            }
    )
    public ResponseEntity<TaskDto> getTaskById(@PathVariable(name = "taskId") UUID taskId) {
        var task = taskService.getTask(taskId);

        return ResponseEntity.ok().body(taskMapper.toDto(task));
    }

    @PostMapping
    @PreAuthorize("@customSecurityExpression.canCreate(#taskDto.profileId)")
    @Operation(
            summary = "Create new Task",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created task"),
                    @ApiResponse(responseCode = "400", description = "Task ID must not be provided"),
                    @ApiResponse(responseCode = "400", description = "Validation failed")
            }
    )
    public ResponseEntity<TaskDto> createTask(@RequestBody @Validated(OnCreate.class) TaskDto taskDto) {

        var task = taskMapper.toEntity(taskDto);
        task = taskService.create(task);

        return ResponseEntity
                .created(URI.create("/api/v1/task/" + task.getTaskId()))
                .body(taskMapper.toDto(task));
    }

    @PutMapping("/{taskId}")
    @PreAuthorize("@customSecurityExpression.isOwner(#taskId)")
    @Operation(
            summary = "Update a specific task by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated task"),
                    @ApiResponse(responseCode = "400", description = "ID in path and body must match"),
                    @ApiResponse(responseCode = "400", description = "Validation failed")
            }
    )
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable(name = "taskId") UUID taskId,
            @RequestBody @Validated(OnUpdate.class) TaskDto taskDto
    ) {
        var task = taskMapper.toEntity(taskDto);
        task = taskService.update(taskId, task);

        return ResponseEntity.ok().body(taskMapper.toDto(task));
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("@customSecurityExpression.hasPermissionTask(#taskId)")
    @Operation(
            summary = "Delete a specific task by its ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted task")
            }
    )
    public ResponseEntity<HttpStatus> deleteTask(@PathVariable UUID taskId) {
        taskService.delete(taskId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reassign/{taskId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Reassign existed Task to other Profile",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Task successfully reassigned")
            }
    )
    public ResponseEntity<HttpStatus> reassignTask(
            @PathVariable UUID taskId, @RequestParam("newProfileId") UUID newProfileId) {
        taskService.reassign(taskId, newProfileId);
        return ResponseEntity.noContent().build();
    }
}
