package com.kursovaya.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProjectRole {
    ADMINISTRATOR("Администратор"),
    PROJECT_MANAGER("Менеджер"),
    DEVELOPER("Разработчик"),
    TESTER("Тестировщик"),
    ANALYST("Аналитик"),
    BEGINNER("Новичок"),
    CLIENT("Заказчик");

    private final String title;

    public static ProjectRole fromTitle(String projectRole) {
        for (ProjectRole role : values()){
            if (role.title.equals(projectRole)) return role;
        }
        return BEGINNER;
    }
}
