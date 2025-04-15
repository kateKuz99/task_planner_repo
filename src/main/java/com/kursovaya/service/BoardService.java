package com.kursovaya.service;

import com.kursovaya.dto.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BoardService {
    Mono<BoardDto> get(Long boardId);
    Mono<BoardDto> rename(Long boardId, BoardDto boardDto);
    Flux<BoardDto> getAll(Long workspaceId);
    Mono<BoardDto> add(BoardDto boardDto,Authentication authentication);
    Mono<ServerResponse> delete(Long boardId,Authentication authentication);

    Mono<Void> deleteAllByWorkspaceId(Long workspaceId);
}


