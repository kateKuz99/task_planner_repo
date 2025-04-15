package com.kursovaya.mapper;

import com.kursovaya.model.TaskSeverity;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class TaskSeverityMapper {
    @Named("taskSeverityToString")
    public String taskSeverityToString(TaskSeverity taskSeverity) {
        return taskSeverity != null ? taskSeverity.getTitle() : null;
    }

    @Named("stringToTaskSeverity")
    public TaskSeverity stringToTaskSeverity(String title) {
        if(title != null){
            String validate_title = title.substring(0, 1).toUpperCase() + title.substring(1).toLowerCase();
            return TaskSeverity.fromTitle(validate_title);
        }
        else return null;

    }
}
