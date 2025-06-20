package ru.zuzex.practice.taskms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.zuzex.practice.taskms.model.Task;
import ru.zuzex.practice.taskms.service.TaskService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;


    @GetMapping
    public ResponseEntity<List<Task>> getTasksByAccountId(@RequestParam(name = "accountId") UUID accountId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(taskService.getAllByAccountId(accountId));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTaskById(@PathVariable(name = "taskId") UUID taskId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(taskService.getById(taskId));
    }

    @PostMapping
    public ResponseEntity<Task> create(@RequestBody Task task){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskService.create(task));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Task> update(@PathVariable(name = "taskId") UUID taskId, @RequestBody Task task){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(taskService.update(taskId, task));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<HttpStatus> delete(@PathVariable UUID taskId){
        taskService.deleteById(taskId);
        return ResponseEntity.ok().build();
    }
}
