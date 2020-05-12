package com.example.bachelor.impl;

import com.example.bachelor.api.ProjectInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Mapper
interface ProjectMapper {

    @Mapping(target = "members", source = "members", qualifiedByName = "mapMembers")
    ProjectInfo map(ProjectEntity project);

    @Named("mapMembers")
    default Set<String> mapMembers(Set<ProjectUserEntity> members) {
        return members.stream().map(ProjectUserEntity::getUsername).collect(toSet());
    }
}
