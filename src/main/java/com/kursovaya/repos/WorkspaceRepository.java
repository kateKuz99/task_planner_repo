package com.kursovaya.repos;

import com.kursovaya.model.Workspace;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface WorkspaceRepository extends R2dbcRepository<Workspace, Long> {
}
