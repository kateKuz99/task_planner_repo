package com.kursovaya.service;

import com.kursovaya.dto.*;
import com.kursovaya.model.ProjectRole;
import com.kursovaya.model.Workspace;
import org.springframework.security.core.Authentication;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WorkspaceService {
    Mono<Workspace> create(WorkspaceDto workspaceDto, Authentication authentication);
    Mono<Workspace> rename(WorkspaceDto workspaceDto, Long workspaceId, Authentication authentication);
    Mono<Void> delete(Long workspaceId,Authentication authentication);
    Mono<ServerResponse> inviteUser(Long workspaceId, InviteDto inviteDto,Authentication authentication);
    Mono<ProjectDto> setRole(Long workspaceId, Long userId, ProjectRole projectRole,Authentication authentication);
    Mono<ServerResponse> deleteUser(Long workspaceId,Long userId,Authentication authentication);
    Flux<ProjectUserDto> getAllUsers(Long workspaceId);
    Mono<TeamResponseDto> createTeam(Long workspaceId,TeamRequestDto teamRequestDto,Authentication authentication);
    Mono<TeamResponseDto> addUsersToTeam(Long workspaceId,Long teamId,TeamChangeDto teamChangeDto,Authentication authentication);
    Mono<TeamResponseDto> deleteUsersFromTeam(Long workspaceId,Long teamId,TeamChangeDto teamChangeDto,Authentication authentication);
    Mono<ServerResponse> deleteTeam(Long workspaceId,Long teamId,Authentication authentication);
    Flux<TeamResponseDto> getAllTeams(Long workspaceId);
    Mono<TeamResponseDto> getTeam(Long workspaceId, Long teamId);
    Flux<WorkspaceDto> getAllWorkspaces(Authentication authentication);
}
