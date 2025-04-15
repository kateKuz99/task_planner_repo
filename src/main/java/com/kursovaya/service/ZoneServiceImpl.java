package com.kursovaya.service;

import com.kursovaya.dto.ZoneDto;
import com.kursovaya.mapper.ZoneMapper;
import com.kursovaya.model.TaskStatus;
import com.kursovaya.repos.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class ZoneServiceImpl implements ZoneService {
    private final ZoneRepository zoneRepository;
    private final TaskService taskService;
    private final ZoneMapper zoneMapper;

    @Override
    public Mono<ZoneDto> get(Long zoneId) {
        return zoneRepository.findById(zoneId)
                .map(zoneMapper::toDto);
    }

    @Override
    public Flux<ZoneDto> getAll(Long boardId) {
        return zoneRepository.findAllByBoardId(boardId)
                .map(zoneMapper::toDto);
    }

    @Override
    public Mono<Void> createBaseZones(Long boardId) {
        return Flux.fromIterable(Arrays.stream(TaskStatus.values()).toList())
                .flatMap(taskStatus -> {
                    return addZone(new ZoneDto().toBuilder()
                            .name(taskStatus.getTitle())
                            .boardId(boardId)
                            .taskStatus(taskStatus.getTitle())
                            .build());

                }).then();
    }

    @Override
    public Mono<ZoneDto> addZone(ZoneDto zoneDto) {
        return zoneRepository.save(zoneMapper.toEntity(zoneDto))
                .map(zoneMapper::toDto);
    }

    @Override
    public Mono<ZoneDto> renameZone(Long zoneId, ZoneDto zoneDto) {
        return zoneRepository.findById(zoneId)
                .flatMap(zone -> {
                    zone.setName(zoneDto.getName());
                    return zoneRepository.save(zone)
                            .map(zoneMapper::toDto);
                });
    }

    @Override
    public Mono<Void> delete(Long zoneId) {
        return taskService.deleteAll(zoneId)
                .then(zoneRepository.deleteById(zoneId));
    }



    @Override
    public Mono<Void> deleteAll(Long boardId) {
        return zoneRepository.findAllByBoardId(boardId)
                .flatMap(zone -> {
                    return delete(zone.getId());
                })
                .then();
    }

    @Override
    public Mono<ZoneDto>  getZoneByBoardIdAndTaskStatus(Long boardId, TaskStatus taskStatus) {
        return zoneRepository.findByBoardIdAndTaskStatus(boardId, taskStatus)
                .flatMap(zoneId -> zoneRepository.findById(zoneId)) // получаем Zone по ID
                .map(zoneMapper::toDto); // маппим в DTO
    }
}
