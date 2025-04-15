package com.kursovaya.service;

import com.kursovaya.dto.ProjectUserDto;
import com.kursovaya.model.Project;
import com.kursovaya.model.ProjectRole;
import com.kursovaya.model.UserEntity;
import com.kursovaya.model.Workspace;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface ProjectService {
    Mono<Project> save(Project project);

    Flux<Project> findAllByWorkspaceId(Long workspaceId);

    Mono<Project> findByWorkspaceIdAndUserId(Long workspaceId, Long userId);

    Mono<Boolean> checkProjectRole(Long workspaceId, Authentication authentication, ProjectRole... projectRoles);

    Mono<ProjectUserDto> getProjectUserDto(Project project);

    Mono<Project> findById(Long projectId);

    Mono<UserEntity> getUserById(Long projectId);

    Flux<Project> getWorkspacesByUserId(Long userId);

    Mono<Void> deleteAllByWorkspaceId(Long workspaceId);
}
