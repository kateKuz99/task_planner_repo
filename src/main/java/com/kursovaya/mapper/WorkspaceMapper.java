package com.kursovaya.mapper;

import com.kursovaya.dto.WorkspaceDto;
import com.kursovaya.model.Workspace;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorkspaceMapper extends Mappable<Workspace, WorkspaceDto>{
}
