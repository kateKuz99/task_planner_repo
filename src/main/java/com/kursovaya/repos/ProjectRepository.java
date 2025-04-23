package com.kursovaya.repos;

import com.kursovaya.model.Project;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProjectRepository extends R2dbcRepository<Project,Long> {
    @Query("SELECT * FROM projects WHERE workspace_id = :workspaceId AND user_id = :userId")
    Mono<Project> findByWorkspaceIdAndUserId(Long workspaceId,Long userId);

    Flux<Project> findAllByWorkspaceId(Long workspaceId);
    Flux<Project> findAllByUserId(Long userId);



    Mono<Void> deleteAllByWorkspaceId(Long workspaceId);
}
