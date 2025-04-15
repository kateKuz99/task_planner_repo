package com.kursovaya.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "tasks")
public class Task {
    @Id
    private Long id;
    private String description;
    private TaskSeverity severity;
    private LocalDateTime createdAt;
    private LocalDateTime deadlineAt;
    private Long zoneId;
}
