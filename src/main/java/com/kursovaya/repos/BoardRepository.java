package com.kursovaya.repos;

import com.kursovaya.model.Board;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BoardRepository extends R2dbcRepository<Board,Long> {
    Mono<Void> deleteAllByWorkspaceId(Long workspaceId);

    Flux<Board> findAllByWorkspaceId(Long workspaceId);
}
