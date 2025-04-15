package com.kursovaya.controller;

import com.kursovaya.dto.*;
import com.kursovaya.mapper.WorkspaceMapper;
import com.kursovaya.model.ProjectRole;
import com.kursovaya.service.WorkspaceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WorkspaceController {
    private final WorkspaceService workspaceService;
    private final WorkspaceMapper workspaceMapper;

    @PostMapping
    @Operation(summary = "Create new workspace")
    public Mono<WorkspaceDto> createWorkspace(@RequestBody WorkspaceDto workspaceDto, Authentication authentication){
        return workspaceService.create(workspaceDto, authentication)
                .map(workspaceMapper::toDto);
    }

    @PatchMapping("/{workspaceId}")
    @Operation(summary = "Rename workspace")
    public Mono<WorkspaceDto> renameWorkspace(@PathVariable Long workspaceId, @RequestBody WorkspaceDto workspaceDto,Authentication authentication){
        return workspaceService.rename(workspaceDto,workspaceId,authentication)
                .map(workspaceMapper::toDto);
    }

    @DeleteMapping("/{workspaceId}")
    @Operation(summary = "Delete workspace")
    public Mono<Void> deleteWorkspace(@PathVariable Long workspaceId,Authentication authentication){
        return workspaceService.delete(workspaceId,authentication);
    }

    @PostMapping("/{workspaceId}/users/invite")
    @Operation(summary = "Invite new user to workspace")
    public Mono<ServerResponse> inviteUser(@PathVariable Long workspaceId, @RequestBody InviteDto inviteDto,Authentication authentication){
        return workspaceService.inviteUser(workspaceId,inviteDto,authentication);
    }

    @PatchMapping("/{workspaceId}/users/{userId}")
    @Operation(summary = "Set new project role for user")
    public Mono<ProjectDto> setProjectRole(@PathVariable Long workspaceId, @PathVariable Long userId, @RequestBody ProjectRoleDto projectRoleDto,Authentication authentication){
        return workspaceService.setRole(workspaceId,userId, ProjectRole.fromTitle(projectRoleDto.getProjectRole()),authentication);
    }

    @DeleteMapping("/{workspaceId}/users/{userid}")
    @Operation(summary = "Delete user from workspace")
    public Mono<ServerResponse> deleteUserFromWorkspace(@PathVariable Long workspaceId, @PathVariable Long userId,Authentication authentication){
        return workspaceService.deleteUser(workspaceId,userId,authentication);
    }

    @GetMapping("/{workspaceId}/users")
    @Operation(summary = "Get all users from workspace")
    public Flux<ProjectUserDto> getAllUsersFromWorkspace(@PathVariable Long workspaceId){
        return workspaceService.getAllUsers(workspaceId);
    }

    @PostMapping("/{workspaceId}/teams")
    @Operation(summary = "Create new team")
    public Mono<TeamResponseDto> createTeam(@PathVariable Long workspaceId, @RequestBody TeamRequestDto teamRequestDto,Authentication authentication){
        return workspaceService.createTeam(workspaceId,teamRequestDto,authentication);
    }

    @PostMapping("/{workspaceId}/teams/{teamId}/users")
    @Operation(summary = "Add users to team")
    public Mono<TeamResponseDto> addUsersToTeam(@PathVariable Long workspaceId, @PathVariable Long teamId, @RequestBody TeamChangeDto teamChangeDto,Authentication authentication){
        return workspaceService.addUsersToTeam(workspaceId,teamId,teamChangeDto,authentication);
    }

    @DeleteMapping("/{workspaceId}/teams/{teamId}/users")
    @Operation(summary = "Delete users from team")
    public Mono<TeamResponseDto> deleteUsersFromTeam(@PathVariable Long workspaceId, @PathVariable Long teamId, @RequestBody TeamChangeDto teamChangeDto,Authentication authentication){
        return workspaceService.deleteUsersFromTeam(workspaceId,teamId,teamChangeDto,authentication);
    }

    @DeleteMapping("/{workspaceId}/teams/{teamId}")
    @Operation(summary = "Delete team")
    public Mono<ServerResponse> deleteTeam(@PathVariable Long workspaceId, @PathVariable Long teamId,Authentication authentication){
        return workspaceService.deleteTeam(workspaceId,teamId,authentication);
    }

    @GetMapping("/{workspaceId}/teams")
    @Operation(summary = "Get all teams")
    public Flux<TeamResponseDto> getAllTeams(@PathVariable Long workspaceId){
        return workspaceService.getAllTeams(workspaceId);
    }

    @GetMapping("/{workspaceId}/teams/{teamId}")
    @Operation(summary = "Get all teams")
    public Mono<TeamResponseDto> getAllTeams(@PathVariable Long workspaceId,@PathVariable Long teamId){
        return workspaceService.getTeam(workspaceId,teamId);
    }

    @GetMapping
    @Operation(summary = "Get all user's workspaces")
    public Flux<WorkspaceDto> getAllUsersWorkspaces(Authentication authentication){
        return workspaceService.getAllWorkspaces(authentication);
    }

}
