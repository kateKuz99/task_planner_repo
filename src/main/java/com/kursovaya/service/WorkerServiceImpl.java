package com.kursovaya.service;

import com.kursovaya.dto.WorkerDto;
import com.kursovaya.mapper.WorkerMapper;
import com.kursovaya.model.UserEntity;
import com.kursovaya.model.Worker;
import com.kursovaya.model.Project;
import com.kursovaya.repos.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkerServiceImpl implements WorkerService {
    private final WorkerRepository workerRepository;
    private final WorkerMapper workerMapper;
    private final ProjectService projectService;
    private final MailService mailService;

    @Override
    public Mono<WorkerDto> save(WorkerDto worker) {
        return workerRepository.save(workerMapper.toEntity(worker))
                .flatMap(savedWorker -> projectService.getUserById(worker.getProjectId())
                        .flatMap(userEntity -> {
                            String message = String.format("Hello %s!\nYou have been assigned a new task.", userEntity.getUsername());
                            mailService.sendSimpleEmail(userEntity.getEmail(), "Task notification", message);
                            return Mono.just(savedWorker);
                        }))
                .map(workerMapper::toDto);
    }

    @Override
    public Mono<Void> delete(WorkerDto worker) {
        return workerRepository.deleteAllByTaskIdAndProjectId(worker.getProjectId(), worker.getTaskId());
    }

    @Override
    public Flux<WorkerDto> getAllByTaskId(Long taskId) {
        return workerRepository.findAllByTaskId(taskId)
                .map(workerMapper::toDto);
    }

    @Override
    public Flux<Worker> getAllByUserIdAndWorkspaceId(Long workspaceId, Long userId) {
        return projectService.findAllByWorkspaceId(workspaceId)
                .filter(project -> project.getUserId().equals(userId))
                .flatMap(project -> workerRepository.findAllByProjectId(project.getId()));
    }

    @Override
    public Mono<Long> getProjectIdsByUserIdAndWorkspaceId(Long workspaceId, Long userId) {
        return projectService.findAllByWorkspaceId(workspaceId)
                .filter(project -> project.getUserId().equals(userId))
                .map(Project::getId) // Получаем ID проектов
                .next(); // Получаем первое значение
    }
}
