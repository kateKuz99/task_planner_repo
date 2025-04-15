package com.kursovaya.service;

import com.kursovaya.dto.*;
import com.kursovaya.exception.AccessException;
import com.kursovaya.mapper.BoardMapper;
import com.kursovaya.model.Board;
import com.kursovaya.model.ProjectRole;
import com.kursovaya.model.Worker;
import com.kursovaya.repos.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final TeamService teamService;
    private final ZoneService zoneService;
    private final ProjectService projectService;
    private final UserService userService;
    private final WorkerService workerService;
    private final BoardMapper boardMapper;

    private final Logger logger = LogManager.getLogger(BoardServiceImpl.class);

    @Override
    public Mono<BoardDto> get(Long boardId) {
        logger.info("Get board ID -" + boardId);
        return boardRepository.findById(boardId)
                .map(boardMapper::toDto);
    }

    @Override
    public Mono<BoardDto> rename(Long boardId, BoardDto boardDto) {
        return get(boardId)
                .map(boardMapper::toEntity)
                .flatMap(board -> {
                    logger.info("Border ID - " + board.getId() + " renamed from " + board.getName() + " to " + boardDto.getName());
                    board.setName(boardDto.getName());
                    return boardRepository.save(board);
                })
                .map(boardMapper::toDto);
    }

    @Override
    public Flux<BoardDto> getAll(Long workspaceId) {
        logger.info("Get all boards of workspace ID - " + workspaceId);
        return boardRepository.findAll()
                .filter(board->board.getWorkspaceId().equals(workspaceId))
                .map(boardMapper::toDto);
    }

    @Override
    public Mono<BoardDto> add(BoardDto boardDto, Authentication authentication) {
        return projectService.checkProjectRole(boardDto.getWorkspaceId(), authentication, ProjectRole.ADMINISTRATOR)
                .flatMap(flag -> {
                    if (flag) {
                        logger.info("New board was created by dto - " + boardDto);
                        return boardRepository.save(new Board()
                                        .toBuilder()
                                        .workspaceId(boardDto.getWorkspaceId())
                                        .name(boardDto.getName())
                                        .build())
                                .flatMap(board -> zoneService.createBaseZones(board.getId())
                                        .thenReturn(board))
                                .map(boardMapper::toDto);
                    } else return Mono.error(new AccessException());
                });
    }

    @Override
    public Mono<ServerResponse> delete(Long boardId, Authentication authentication) {
        return boardRepository.findById(boardId)
                .flatMap(board -> projectService.checkProjectRole(board.getWorkspaceId(), authentication, ProjectRole.ADMINISTRATOR)
                        .flatMap(flag -> {
                            logger.info("Deleting board ID - " + boardId);
                            if (flag) {
                                return
                                        zoneService.deleteAll(boardId)
                                                .then(boardRepository.delete(board))
                                                .then(ServerResponse.ok().bodyValue("Board successfully deleted"))
                                                .onErrorResume(e -> ServerResponse.badRequest().bodyValue("Exception during deleting board - " + e));
                            } else {
                                return Mono.error(new AccessException());
                            }
                        })
                );
    }

    @Override
    public Mono<Void> deleteAllByWorkspaceId(Long workspaceId) {
        return boardRepository.findAllByWorkspaceId(workspaceId)
                .flatMap(board -> {
                    return zoneService.deleteAll(board.getId());
                })
                .then(boardRepository.deleteAllByWorkspaceId(workspaceId));
    }

}
