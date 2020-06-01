package com.example.bachelor.impl;

import com.example.bachelor.api.EpicInfo;
import com.example.bachelor.api.ProjectDetails;
import com.example.bachelor.api.ProjectInfo;
import org.mapstruct.Mapper;

@Mapper
interface ProjectMapper {

    ProjectInfo map(ProjectEntity project);

    ProjectDetails mapToDetals(ProjectEntity project);

    EpicInfo mapEpic(EpicEntity epic);

    EpicEntity mapEpic(EpicInfo epic, int projectId);
}
