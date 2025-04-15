package com.kursovaya.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserUpdateDto {
    private Long id;
    private String username;
    private String email;
    private String firstName; // Для обновления имени
    private String lastName;  // Для обновления фамилии
    private String password;  // Для обновления пароля (если нужно)
}
