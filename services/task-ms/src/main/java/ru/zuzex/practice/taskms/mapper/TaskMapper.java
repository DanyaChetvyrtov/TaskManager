package ru.zuzex.practice.taskms.mapper;

import org.mapstruct.Mapper;
import ru.zuzex.practice.taskms.dto.TaskDto;
import ru.zuzex.practice.taskms.model.Task;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskDto toDto(Task account);

    Task toEntity(TaskDto accountDto);
}
