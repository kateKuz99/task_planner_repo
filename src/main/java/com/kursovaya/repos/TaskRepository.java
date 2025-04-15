package com.kursovaya.repos;

import com.kursovaya.dto.TaskDto;
import com.kursovaya.model.Task;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TaskRepository extends R2dbcRepository<Task,Long> {
    Flux<Task> findAllByZoneId(Long zoneId);

    Mono<Void> deleteAllByZoneId(Long zoneId);

}
