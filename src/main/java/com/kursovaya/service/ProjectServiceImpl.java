package com.kursovaya.service;

import com.kursovaya.dto.ProjectUserDto;
import com.kursovaya.model.Project;
import com.kursovaya.model.ProjectRole;
import com.kursovaya.model.UserEntity;
import com.kursovaya.model.Workspace;
import com.kursovaya.repos.ProjectRepository;
import com.kursovaya.security.CustomPrincipal;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final UserService userService;

    private final Logger logger = LogManager.getLogger(ProjectServiceImpl.class);

    @Override
    public Mono<Project> save(Project project) {
        logger.info("Saving project - "+project.toString());
        return projectRepository.save(project);
    }

    @Override
    public Flux<Project> findAllByWorkspaceId(Long workspaceId) {
        logger.info("Getting all projects of workspace ID - "+workspaceId);
        return projectRepository.findAllByWorkspaceId(workspaceId);
    }

    @Override
    public Mono<Project> findByWorkspaceIdAndUserId(Long workspaceId, Long userId) {
        return projectRepository.findByWorkspaceIdAndUserId(workspaceId, userId);
    }

    @Override
    public Mono<Boolean> checkProjectRole(Long workspaceId, Authentication authentication, ProjectRole... projectRoles) {
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        logger.info("Check " + principal.getName() + "'s rights");
        return findByWorkspaceIdAndUserId(workspaceId, principal.getId())
                .map(project -> {
                    for (ProjectRole role : projectRoles) {
                        if (role.equals(project.getProjectRole())) {
                            logger.info("Access is allowed");
                            return true;
                        }
                    }
                    logger.warn("Access is denied");
                    return false;
                })
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<ProjectUserDto> getProjectUserDto(Project project) {
        return userService.getUserById(project.getUserId())
                .flatMap(user -> {
                    return Mono.just(ProjectUserDto.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .email(user.getEmail())
                            .projectRole(project.getProjectRole().getTitle())
                            .build());
                });

    }

    @Override
    public Mono<Project> findById(Long projectId) {
        return projectRepository.findById(projectId);
    }

    @Override
    public Mono<UserEntity> getUserById(Long projectId) {
        return projectRepository.findById(projectId)
                .flatMap(project -> userService.getUserById(project.getUserId()));
    }

    @Override
    public Flux<Project> getWorkspacesByUserId(Long userId) {
        return projectRepository.findAllByUserId(userId);
    }

    @Override
    public Mono<Void> deleteAllByWorkspaceId(Long workspaceId) {
        return projectRepository.deleteAllByWorkspaceId(workspaceId);
    }
}
