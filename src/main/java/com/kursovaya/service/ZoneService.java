package com.kursovaya.service;

import com.kursovaya.dto.ZoneDto;
import com.kursovaya.model.TaskStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ZoneService {
    Mono<ZoneDto> get(Long zoneId);
    Flux<ZoneDto> getAll(Long boardId);
    Mono<Void> createBaseZones(Long boardId);
    Mono<ZoneDto> addZone(ZoneDto zoneDto);
    Mono<ZoneDto> renameZone(Long zoneId, ZoneDto zoneDto);
    Mono<Void> delete(Long zoneId);
    Mono<Void> deleteAll(Long boardId);
    Mono<ZoneDto> getZoneByBoardIdAndTaskStatus(Long boardId, TaskStatus taskStatus);
}
