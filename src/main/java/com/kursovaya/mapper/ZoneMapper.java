package com.kursovaya.mapper;

import com.kursovaya.dto.ZoneDto;
import com.kursovaya.model.Zone;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = TaskStatusMapper.class)
public interface ZoneMapper extends Mappable<Zone, ZoneDto>{
    @Mapping(source = "taskStatus", target="taskStatus", qualifiedByName = "stringToTaskStatus")
    Zone toEntity(ZoneDto dto);

    @Mapping(source = "taskStatus", target = "taskStatus", qualifiedByName = "taskStatusToString")
    ZoneDto toDto(Zone entity);
}
