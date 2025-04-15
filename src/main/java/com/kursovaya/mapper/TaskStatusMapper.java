package com.kursovaya.mapper;

import com.kursovaya.model.TaskStatus;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class TaskStatusMapper {

    @Named("taskStatusToString")
    public String taskStatusToString(TaskStatus taskStatus) {
        return taskStatus != null ? taskStatus.getTitle() : null;
    }

    @Named("stringToTaskStatus")
    public TaskStatus stringToTaskStatus(String title) {
        if(title != null){
            String validate_title = title.substring(0, 1).toUpperCase() + title.substring(1).toLowerCase();
            return TaskStatus.fromTitle(validate_title);
        }
        else return null;

    }

}
