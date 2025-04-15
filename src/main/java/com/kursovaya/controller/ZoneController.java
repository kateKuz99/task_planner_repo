package com.kursovaya.controller;

import com.kursovaya.dto.ZoneDto;
import com.kursovaya.model.TaskStatus;
import com.kursovaya.service.ZoneService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ZoneController {
    private final ZoneService zoneService;

    @GetMapping("/zones/{zoneId}")
    @Operation(summary = "Get zone by id")
    public Mono<ZoneDto> get(@PathVariable Long zoneId){
        return zoneService.get(zoneId);
    }

    @GetMapping("/boards/{boardId}/zones")
    @Operation(summary = "Get all zones of the board")
    public Flux<ZoneDto> getAll(@PathVariable Long boardId) {
        return zoneService.getAll(boardId);
    }

    @PostMapping("/zones")
    @Operation(summary = "Add new zone to board")
    public Mono<ZoneDto> addZone(@RequestBody ZoneDto zoneDto){
        return zoneService.addZone(zoneDto);
    }

    @PatchMapping("/zones/{zoneId}")
    @Operation(summary = "Rename zone by id")
    public Mono<ZoneDto> renameZone(@PathVariable Long zoneId,@RequestBody ZoneDto zoneDto){
        return zoneService.renameZone(zoneId,zoneDto);
    }

    @GetMapping("/{boardId}/zones")
    @Operation(summary = "Get zone by board ID and task status")
    public Mono<ZoneDto> getZoneByBoardIdAndTaskStatus(
            @PathVariable Long boardId,
            @RequestParam TaskStatus taskStatus) {
        return zoneService.getZoneByBoardIdAndTaskStatus(boardId, taskStatus);
    }

    @DeleteMapping("/zones/{zoneId}")
    @Operation(summary = "Deleting zones")
    public Mono<Void> deleteZone(@PathVariable Long zoneId){
        return zoneService.delete(zoneId);
    }

}
