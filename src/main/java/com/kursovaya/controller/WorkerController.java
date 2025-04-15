package com.kursovaya.controller;

import com.kursovaya.dto.WorkerDto;
import com.kursovaya.model.Project;
import com.kursovaya.model.Worker;
import com.kursovaya.service.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workers")
public class WorkerController {
    private final WorkerService workerService;

    @PostMapping
    Mono<WorkerDto> save(@RequestBody WorkerDto worker){return workerService.save(worker);}

    @DeleteMapping
    Mono<Void> delete(@RequestBody WorkerDto worker){return workerService.delete(worker);}

    @GetMapping("/{taskId}")
    Flux<WorkerDto> getAllByTaskId(@PathVariable Long taskId){return workerService.getAllByTaskId(taskId);}

    @GetMapping("/{workspaceId}/{taskId}")
    Mono<Long> getProjectIdsByUserIdAndWorkspaceId(@PathVariable Long workspaceId, @PathVariable Long taskId){return workerService.getProjectIdsByUserIdAndWorkspaceId(workspaceId,taskId);}

}
