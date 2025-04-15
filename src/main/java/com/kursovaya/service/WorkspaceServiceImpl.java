package com.kursovaya.service;

import com.kursovaya.dto.*;
import com.kursovaya.exception.AccessException;
import com.kursovaya.mapper.ProjectMapper;
import com.kursovaya.mapper.WorkspaceMapper;
import com.kursovaya.model.*;
import com.kursovaya.repos.WorkspaceRepository;
import com.kursovaya.security.CustomPrincipal;
import com.kursovaya.sql.TeamUser;
import com.kursovaya.sql.TeamUserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {
    private final MailService mailService;
    private final UserService userService;
    private final WorkspaceRepository workspaceRepository;
    private final TeamService teamService;
    private final ProjectService projectService;
    private final BoardService boardService;
    private final TeamUserRepository teamUserRepository;
    private final ProjectMapper projectMapper;
    private final WorkspaceMapper workspaceMapper;

    private final Logger logger = LogManager.getLogger(WorkspaceServiceImpl.class);

    @Override
    public Mono<Workspace> create(WorkspaceDto workspaceDto, Authentication authentication) {
        return workspaceRepository.save(new Workspace().toBuilder()
                        .name(workspaceDto.getName())
                        .build())
                .flatMap(workspace -> {
                    logger.info("New workspace was created - " + workspace.toString());
                    CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();

                    Project project = new Project().toBuilder()
                            .projectRole(ProjectRole.ADMINISTRATOR)
                            .workspaceId(workspace.getId())
                            .userId(principal.getId())
                            .build();

                    logger.info("Saving new project - " + project.toString());
                    return projectService.save(project).thenReturn(workspace);
                });
    }

    @Override
    public Mono<Workspace> rename(WorkspaceDto workspaceDto, Long workspaceId, Authentication authentication) {
        return projectService.checkProjectRole(workspaceId, authentication, ProjectRole.ADMINISTRATOR)
                .flatMap(flag -> {
                    if (flag)
                        return workspaceRepository.findById(workspaceId)
                                .flatMap(workspace -> {
                                    logger.info("Change workspace name from " + workspace.getName() + " to " + workspaceDto.getName());
                                    workspace.setName(workspaceDto.getName());
                                    return workspaceRepository.save(workspace);
                                });
                    else return Mono.error(new AccessException());
                });
    }

    @Override
    public Mono<Void> delete(Long workspaceId, Authentication authentication) {
        return boardService.deleteAllByWorkspaceId(workspaceId)
                .then(projectService.deleteAllByWorkspaceId(workspaceId))
                .then(teamUserRepository.deleteAllByWorkspaceId(workspaceId))
                .then(workspaceRepository.deleteById(workspaceId));
    }

    @Override
    public Mono<ServerResponse> inviteUser(Long workspaceId, InviteDto inviteDto, Authentication authentication) {
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();

        logger.info("Attempting to invite user with email: " + inviteDto.getEmail() + " to workspace with ID: " + workspaceId);
        return projectService.checkProjectRole(workspaceId, authentication, ProjectRole.ADMINISTRATOR)
                .flatMap(flag -> {
                    if (flag) {
                        logger.info("User " + principal.getName() + " has ADMINISTRATOR role, proceeding with invitation");

                        return userService.getUserByEmail(inviteDto.getEmail())
                                .flatMap(userEntity -> {
                                    String message = String.format("Hello %s!\nUser %s invited you to their project. Now you can build the project together!",
                                            userEntity.getUsername(), principal.getName());

                                    logger.info("Sending invitation email to " + inviteDto.getEmail());

                                    mailService.sendSimpleEmail(inviteDto.getEmail(), "Invite to workspace", message);

                                    Project project = new Project().toBuilder()
                                            .workspaceId(workspaceId)
                                            .userId(userEntity.getId())
                                            .projectRole(ProjectRole.BEGINNER)
                                            .build();

                                    logger.info("Saving project for user ID: " + userEntity.getId() + " in workspace ID: " + workspaceId);

                                    return projectService.save(project)
                                            .then(Mono.just(userEntity));
                                })
                                .flatMap(userEntity -> {
                                    logger.info("User " + inviteDto.getEmail() + " was successfully invited");
                                    return ServerResponse.ok().bodyValue("User was invited");
                                })
                                .switchIfEmpty(ServerResponse.badRequest().bodyValue("No such user"));
                    } else {
                        logger.warn("User " + principal.getName() + " does not have ADMINISTRATOR role");
                        return Mono.error(new AccessException());
                    }
                })
                .onErrorResume(e -> {
                    logger.error("Error occurred while inviting user: " + e.getMessage());
                    return ServerResponse.badRequest().bodyValue("Bad request: " + e.getMessage());
                });
    }

    @Override
    public Mono<ProjectDto> setRole(Long workspaceId, Long userId, ProjectRole projectRole, Authentication authentication) {
        return projectService.checkProjectRole(workspaceId, authentication, ProjectRole.ADMINISTRATOR)
                .flatMap(flag -> {
                    if (flag) return projectService.findByWorkspaceIdAndUserId(workspaceId, userId)
                            .flatMap(project -> {
                                logger.info("User's (" + userId + ") project role was changed from " + project.getProjectRole() + " to " + projectRole);
                                project.setProjectRole(projectRole);
                                return projectService.save(project).map(projectMapper::toDto);
                            });
                    else return Mono.error(new AccessException());
                });
    }

    @Override
    public Mono<ServerResponse> deleteUser(Long workspaceId, Long userId, Authentication authentication) {
        return projectService.checkProjectRole(workspaceId, authentication, ProjectRole.ADMINISTRATOR)
                .flatMap(flag -> {
                    if (flag) return teamUserRepository.findAllByWorkspaceId(workspaceId)
                            .filter(teamUser -> teamUser.getUserId().equals(userId))
                            .flatMap(teamUser -> teamUserRepository.deleteByAllParameters(workspaceId, teamUser.getTeamId(), teamUser.getUserId()))
                            .then(ServerResponse.ok().bodyValue("User successfully deleted"))
                            .doOnError(e -> ServerResponse.badRequest().bodyValue("Exception during deleting user " + e.getMessage()));
                    else return Mono.error(new AccessException());
                });
    }

    @Override
    public Flux<ProjectUserDto> getAllUsers(Long workspaceId) {
        logger.info("Service show all users from workspace ID - " + workspaceId);
        return projectService.findAllByWorkspaceId(workspaceId)
                .flatMap(projectService::getProjectUserDto);
    }

    @Override
    public Mono<TeamResponseDto> createTeam(Long workspaceId, TeamRequestDto teamRequestDto, Authentication authentication) {
        return projectService.checkProjectRole(workspaceId, authentication, ProjectRole.ADMINISTRATOR)
                .flatMap(flag -> {
                    if (flag)
                        return teamService.save(new Team().toBuilder()
                                        .name(teamRequestDto.getName())
                                        .build())
                                .flatMap(team -> {
                                    logger.info("New team with name " + team.getName() + " was created");
                                    return Flux.fromIterable(teamRequestDto.getUsersId())
                                            .flatMap(userId -> userService.getUserById(userId)
                                                    .flatMap(userEntity -> {
                                                        logger.info("Team was added to workspace ID " + workspaceId);
                                                        return teamUserRepository.save(new TeamUser().toBuilder()
                                                                .workspaceId(workspaceId)
                                                                .userId(userId)
                                                                .teamId(team.getId())
                                                                .build());
                                                    }))
                                            .then(getUsersFromTeam(workspaceId, team.getId()));
                                });
                    else return Mono.error(new AccessException());
                });

    }

    @Override
    public Mono<TeamResponseDto> addUsersToTeam(Long workspaceId, Long teamId, TeamChangeDto teamChangeDto, Authentication authentication) {
        return projectService.checkProjectRole(workspaceId, authentication, ProjectRole.ADMINISTRATOR)
                .flatMap(flag -> {
                    if (flag)
                        return Flux.fromIterable(teamChangeDto.getUsersId())
                                .flatMap(userId -> userService.getUserById(userId)
                                        .flatMap(user -> {
                                            logger.info("Users was added to team ID " + teamId);
                                            return teamUserRepository.save(new TeamUser().toBuilder()
                                                    .teamId(teamId)
                                                    .userId(userId)
                                                    .workspaceId(workspaceId)
                                                    .build());
                                        })
                                ).then(getUsersFromTeam(workspaceId, teamId));
                    else return Mono.error(new AccessException());
                });
    }


    @Override
    public Mono<TeamResponseDto> deleteUsersFromTeam(Long workspaceId, Long teamId, TeamChangeDto teamChangeDto, Authentication authentication) {
        return projectService.checkProjectRole(workspaceId, authentication, ProjectRole.ADMINISTRATOR)
                .flatMap(flag -> {
                    if (flag)
                        return Flux.fromIterable(teamChangeDto.getUsersId())
                                .flatMap(userId -> userService.getUserById(userId)
                                        .flatMap(user -> {
                                            logger.info("Users was deleted from team ID " + teamId);
                                            return teamUserRepository.deleteByAllParameters(workspaceId, teamId, userId);
                                        })
                                )
                                .then(getUsersFromTeam(workspaceId, teamId));
                    else return Mono.error(new AccessException());
                });
    }

    @Override
    public Mono<ServerResponse> deleteTeam(Long workspaceId, Long teamId, Authentication authentication) {
        return cleanTeam(workspaceId, teamId, authentication)
                .flatMap(aVoid -> teamService.delete(teamId))
                .then(ServerResponse.ok().bodyValue("Team successfully deleted"))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue("Exception during deleting team: " + e.getMessage()));
    }

    @Override
    public Flux<TeamResponseDto> getAllTeams(Long workspaceId) {
        return teamService.getAll()
                .flatMap(team -> {
                    return getTeam(workspaceId, team.getId());
                });
    }

    @Override
    public Mono<TeamResponseDto> getTeam(Long workspaceId, Long teamId) {
        return getUsersFromTeam(workspaceId, teamId);
    }

    @Override
    public Flux<WorkspaceDto> getAllWorkspaces(Authentication authentication) {
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        return projectService.getWorkspacesByUserId(principal.getId())
                .flatMap(project -> workspaceRepository.findById(project.getWorkspaceId()))
                .map(workspaceMapper::toDto);

    }

    private Mono<Void> cleanTeam(Long workspaceId, Long teamId, Authentication authentication) {
        return projectService.checkProjectRole(workspaceId, authentication, ProjectRole.ADMINISTRATOR)
                .flatMap(flag -> {
                    if (flag)
                        return teamUserRepository.findAllByWorkspaceId(workspaceId)
                                .filter(teamUser -> teamUser.getTeamId().equals(teamId))
                                .flatMap(teamUser -> teamUserRepository.deleteByAllParameters(workspaceId, teamUser.getTeamId(), teamUser.getUserId()))
                                .then();
                    else return Mono.error(new AccessException());
                });
    }

    private Mono<TeamResponseDto> collectTeamResponseDto(Long workspaceId, Long teamId, List<UserEntity> users) {
        return teamService.get(teamId)
                .flatMap(team -> collectTeamResponseDto(workspaceId, teamId, team.getName(), users));
    }

    private Mono<TeamResponseDto> collectTeamResponseDto(Long workspaceId, Long teamId, String name, List<UserEntity> userEntities) {
        return Flux.fromIterable(userEntities)
                .flatMap(userEntity -> {
                    return projectService.findByWorkspaceIdAndUserId(workspaceId, userEntity.getId())
                            .flatMap(projectService::getProjectUserDto);
                })
                .collectList()
                .map(projectUserDtos -> {
                    TeamResponseDto teamResponseDto = new TeamResponseDto();
                    teamResponseDto.setWorkspaceId(workspaceId);
                    teamResponseDto.setTeamId(teamId);
                    teamResponseDto.setName(name);
                    teamResponseDto.setUsers(projectUserDtos);
                    return teamResponseDto;
                });
    }

    private Mono<TeamResponseDto> getUsersFromTeam(Long workspaceId, Long teamId) {
        return teamUserRepository.findAllByWorkspaceId(workspaceId)
                .filter(teamUser -> teamUser.getTeamId().equals(teamId))
                .flatMap(teamUser -> userService.getUserById(teamUser.getUserId()))
                .collectList()
                .flatMap(users -> collectTeamResponseDto(workspaceId, teamId, users));

    }
}
