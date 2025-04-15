package com.kursovaya.controller;

import com.kursovaya.dto.TaskDto;
import com.kursovaya.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping("/tasks/{taskId}")
    @Operation(summary = "Get task by id")
    public Mono<TaskDto> getTask(@PathVariable Long taskId){
        return taskService.get(taskId);
    }

    @GetMapping("/zones/{zoneId}/tasks")
    @Operation(summary = "Get all tasks of zone")
    public Flux<TaskDto> getAllTasks(@PathVariable Long zoneId){
        return taskService.getAll(zoneId);
    }

    @PostMapping("/tasks")
    @Operation(summary = "Create task")
    public Mono<TaskDto> createTask(@RequestBody TaskDto taskDto){
        return taskService.create(taskDto);
    }

    @PatchMapping("/tasks/{taskId}")
    @Operation(summary = "Edit task")
    public Mono<TaskDto> editTask(@PathVariable Long taskId, @RequestBody TaskDto taskDto){
        return taskService.edit(taskId,taskDto);
    }


    @DeleteMapping("/tasks/{taskId}")
    @Operation(summary = "Delete task")
    public Mono<Void> deleteTask(@PathVariable Long taskId){
        return taskService.delete(taskId);
    }

    @DeleteMapping("/zones/{zoneId}/tasks")
    @Operation(summary = "Delete all tasks of zone")
    public Mono<Void> deleteAllTasks(@PathVariable Long zoneId){
        return taskService.deleteAll(zoneId);
    }

    @GetMapping("/{workspaceId}/my-tasks")
    @Operation(summary="Get all tasks of this workspace for authenticate user")
    public Flux<TaskDto> getMyTasks(@PathVariable Long workspaceId, Authentication authentication){
        return taskService.getAllByWorkspaceId(workspaceId,authentication);
    }

}
