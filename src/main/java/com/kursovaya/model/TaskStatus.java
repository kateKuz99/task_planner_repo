package com.kursovaya.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskStatus {
    ASSIGNED("Назначена"),
    IN_PROGRESS("В работе"),
    FOR_REVIEW("На проверке"),
    RETURNED_FOR_REVISION("Возвращена на доработку"),
    PENDING("Ожидание"),
    COMPLETED("Завершена"),
    CANCELED("Отменена");

    private final String title;

    public static TaskStatus fromTitle(String taskStatus) {
        for (TaskStatus status : values()){
            if (status.title.equals(taskStatus)) return status;
        }
        return ASSIGNED;
    }
}
