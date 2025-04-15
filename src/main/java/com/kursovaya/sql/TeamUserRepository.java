package com.kursovaya.sql;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TeamUserRepository extends R2dbcRepository<TeamUser, Long> {
    Flux<TeamUser> findAllByWorkspaceId(Long workspaceId);

    @Query("DELETE FROM team_user WHERE workspace_id = :workspaceId AND team_id = :teamId AND user_id = :userId")
    Mono<Void> deleteByAllParameters(Long workspaceId, Long teamId, Long userId);

    Mono<Void> deleteAllByWorkspaceId(Long workspaceId);
}
