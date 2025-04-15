package com.kursovaya.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "zones")
public class Zone {
    @Id
    private Long id;
    private Long boardId;
    private String name;
    private TaskStatus taskStatus;
}
