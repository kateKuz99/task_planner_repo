package com.kursovaya.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProjectUserDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String projectRole;
    private String email;
}
