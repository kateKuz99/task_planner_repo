package com.kursovaya.service;

import com.kursovaya.dto.TaskDto;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TaskService {
    Mono<TaskDto> get(Long taskId);
    Flux<TaskDto> getAll(Long zoneId);
    Mono<TaskDto> create(TaskDto taskDto);
    Mono<TaskDto> edit(Long taskId,TaskDto taskDto);
    Mono<Void> delete(Long taskId);
    Mono<Void> deleteAll(Long zoneId);
    Flux<TaskDto> getAllByWorkspaceId(Long workspaceId, Authentication authentication);
}
