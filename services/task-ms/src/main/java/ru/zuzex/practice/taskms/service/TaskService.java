package ru.zuzex.practice.taskms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zuzex.practice.taskms.model.Task;
import ru.zuzex.practice.taskms.repository.TaskRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {
    private final TaskRepository taskRepository;

    public List<Task> getAllByAccountId(UUID accountId) {
        return taskRepository.findAllByAccountId(accountId);
    }

    public Task getTask(UUID taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    @Transactional
    public Task create(Task task) {
        return taskRepository.save(task);
    }

    @Transactional
    public Task update(UUID taskId, Task task) {
        var existedTask = getTask(taskId);

        existedTask.setTitle(task.getTitle());
        existedTask.setBody(task.getBody());
        existedTask.setCompleted(task.isCompleted());

        return taskRepository.save(existedTask);
    }

    @Transactional
    public void delete(UUID taskId) {
        taskRepository.deleteById(taskId);
    }
}

