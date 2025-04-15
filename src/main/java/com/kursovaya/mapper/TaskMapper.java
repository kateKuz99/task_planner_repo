package com.kursovaya.mapper;

import com.kursovaya.dto.TaskDto;
import com.kursovaya.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = TaskSeverityMapper.class)
public interface TaskMapper extends Mappable<Task, TaskDto>{
    @Mapping(source = "severity", target="severity", qualifiedByName = "stringToTaskSeverity")
    Task toEntity(TaskDto dto);

    @Mapping(source = "severity", target = "severity", qualifiedByName = "taskSeverityToString")
    TaskDto toDto(Task entity);
}
