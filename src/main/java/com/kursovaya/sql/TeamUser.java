package com.kursovaya.sql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("team_user")
public class TeamUser {
    private Long teamId;
    private Long userId;
    private Long workspaceId;
}
