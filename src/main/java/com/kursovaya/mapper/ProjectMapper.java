package com.kursovaya.mapper;

import com.kursovaya.dto.ProjectDto;
import com.kursovaya.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = ProjectRoleMapper.class)
public interface ProjectMapper extends Mappable<Project, ProjectDto>{
    @Mapping(source = "projectRole", target="projectRole", qualifiedByName = "stringToProjectRole")
    Project toEntity(ProjectDto dto);

    @Mapping(source = "projectRole", target = "projectRole", qualifiedByName = "projectRoleToString")
    ProjectDto toDto(Project entity);
}
