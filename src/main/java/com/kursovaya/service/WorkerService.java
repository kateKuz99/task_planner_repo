package com.kursovaya.service;

import com.kursovaya.dto.WorkerDto;
import com.kursovaya.model.UserEntity;
import com.kursovaya.model.Worker;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface WorkerService {
    Mono<WorkerDto> save(WorkerDto worker);

    Mono<Void> delete(WorkerDto worker);

    Flux<WorkerDto> getAllByTaskId(Long taskId);


    Flux<Worker> getAllByUserIdAndWorkspaceId(Long workspaceId, Long id);

    Mono<Long> getProjectIdsByUserIdAndWorkspaceId(Long workspaceId, Long userId);
}
