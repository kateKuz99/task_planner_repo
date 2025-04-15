package com.kursovaya.repos;

import com.kursovaya.model.Worker;
import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WorkerRepository extends R2dbcRepository<Worker,Long> {
    @Query("SELECT * FROM workers WHERE project_id=:projectId AND board_id=:boardId")
    Mono<Worker> findByProjectIdAndBoardId(Long projectId, Long boardId);
    @Query("DELETE FROM workers WHERE project_id=:projectId AND task_id=:taskId")
    Mono<Void> deleteAllByTaskIdAndProjectId(Long projectId,Long taskId);

    Flux<Worker> findAllByTaskId(Long taskId);

    Flux<Worker> findAllByProjectId(Long id);
}
