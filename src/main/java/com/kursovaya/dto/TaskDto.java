package com.kursovaya.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TaskDto {
    private Long id;
    private String description;
    private String severity;
    private Long zoneId;
    private LocalDateTime createdAt;
    private LocalDateTime deadlineAt;
}
