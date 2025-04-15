package com.kursovaya.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TaskSeverity {
    LOW("Низкая"),
    MEDIUM("Умеренная"),
    HIGH("Высокая");

    private final String title;

    public static TaskSeverity fromTitle(String taskSeverity) {
        for (TaskSeverity severity : values()){
            if (severity.title.equals(taskSeverity)) return severity;
        }
        return MEDIUM;
    }
}
