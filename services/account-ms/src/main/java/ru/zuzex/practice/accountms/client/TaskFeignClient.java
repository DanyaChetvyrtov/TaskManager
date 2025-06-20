package ru.zuzex.practice.accountms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.zuzex.practice.accountms.model.Task;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "task-ms")
public interface TaskFeignClient {
    @GetMapping(value = "api/v1/task", consumes = "application/json")
    List<Task> getTasks(@RequestParam(name = "accountId") UUID accountId);
}
