package com.kursovaya.controller;

import com.kursovaya.dto.BoardDto;
import com.kursovaya.service.BoardService;
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
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/boards/{boardId}")
    @Operation(summary = "Get board")
    public Mono<BoardDto> getBoard(@PathVariable Long boardId){
        return boardService.get(boardId);
    }

    @GetMapping("/{workspaceId}/boards")
    @Operation(summary = "Get all boards of workspace")
    public Flux<BoardDto> getAllBoards(@PathVariable Long workspaceId){
        return boardService.getAll(workspaceId);
    }

    @PostMapping("/boards")
    @Operation(summary="Add new board")
    public Mono<BoardDto> addNewBoard(@RequestBody BoardDto boardDto, Authentication authentication){
        return boardService.add(boardDto,authentication);
    }

    @PatchMapping("/boards/{boardId}")
    @Operation(summary = "Rename board")
    public Mono<BoardDto> renameBoard(@PathVariable Long boardId, @RequestBody BoardDto boardDto){
        return boardService.rename(boardId,boardDto);
    }

    @DeleteMapping("/boards/{boardId}")
    @Operation(summary = "Delete board")
    public Mono<ServerResponse> deleteBoard(@PathVariable Long boardId, Authentication authentication){
        return boardService.delete(boardId,authentication);
    }
}
