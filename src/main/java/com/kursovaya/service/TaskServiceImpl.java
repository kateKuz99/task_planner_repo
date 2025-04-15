package com.kursovaya.service;

import com.kursovaya.dto.TaskDto;
import com.kursovaya.mapper.TaskMapper;
import com.kursovaya.model.Task;
import com.kursovaya.model.TaskSeverity;
import com.kursovaya.repos.TaskRepository;
import com.kursovaya.security.CustomPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService{
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final WorkerService workerService;
    @Override
    public Mono<TaskDto> get(Long taskId) {
        return taskRepository.findById(taskId)
                .map(taskMapper::toDto);
    }

    @Override
    public Flux<TaskDto> getAll(Long zoneId) {
        return taskRepository.findAllByZoneId(zoneId)
                .map(taskMapper::toDto);
    }

    @Override
    public Mono<TaskDto> create(TaskDto taskDto) {
        Task task = taskMapper.toEntity(taskDto);
        task.setCreatedAt(LocalDateTime.now());
        return taskRepository.save(task).map(taskMapper::toDto);
    }

    @Override
    public Mono<TaskDto> edit(Long taskId,TaskDto taskDto) {
        return taskRepository.findById(taskId)
                .flatMap(task -> {
                    if(taskDto.getSeverity()!=null) task.setSeverity(TaskSeverity.fromTitle(taskDto.getSeverity()));
                    if(taskDto.getDescription()!=null) task.setDescription(taskDto.getDescription());
                    if(taskDto.getZoneId()!=null) task.setZoneId(taskDto.getZoneId());
                    if(taskDto.getCreatedAt()!=null) task.setCreatedAt(taskDto.getCreatedAt());
                    if(taskDto.getDeadlineAt()!=null) task.setDeadlineAt(taskDto.getDeadlineAt());
                    return taskRepository.save(task)
                            .map(taskMapper::toDto);
                });
    }


    @Override
    public Mono<Void> delete(Long taskId) {
        return workerService.getAllByTaskId(taskId)
                        .flatMap(workerService::delete)
                                .then(taskRepository.deleteById(taskId));
    }

    @Override
    public Mono<Void> deleteAll(Long zoneId) {
        return taskRepository.findAllByZoneId(zoneId)
                .flatMap(task -> delete(task.getId()))
                .then();
    }

    @Override
    public Flux<TaskDto> getAllByWorkspaceId(Long workspaceId, Authentication authentication) {
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        return workerService.getAllByUserIdAndWorkspaceId(workspaceId,principal.getId())
                .flatMap(worker->get(worker.getTaskId()));
    }
}
