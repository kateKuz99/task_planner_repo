package com.kursovaya.mapper;

import com.kursovaya.model.ProjectRole;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class ProjectRoleMapper {
    @Named("projectRoleToString")
    public String projectRoleToString(ProjectRole projectRole) {
        return projectRole != null ? projectRole.getTitle() : null;
    }

    @Named("stringToProjectRole")
    public ProjectRole stringToProjectRole(String title) {
        if(title != null){
            String validate_title = title.substring(0, 1).toUpperCase() + title.substring(1).toLowerCase();
            return ProjectRole.fromTitle(validate_title);
        }
        else return null;

    }
}
