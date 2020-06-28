package com.example.bachelor.impl;

import com.example.bachelor.api.AttachmentInfo;
import com.example.bachelor.api.EpicInfo;
import com.example.bachelor.api.ProjectDetails;
import com.example.bachelor.api.ProjectInfo;
import com.example.bachelor.api.SprintDetails;
import com.example.bachelor.api.SprintInfo;
import com.example.bachelor.api.TaskDetails;
import com.example.bachelor.api.TaskInfo;
import org.mapstruct.Mapper;

@Mapper
interface ProjectMapper {

    ProjectInfo map(ProjectEntity project);

    ProjectDetails mapToDetails(ProjectEntity project);

    EpicInfo mapEpic(EpicEntity epic);

    EpicEntity mapEpic(EpicInfo epic, int projectId);

    SprintEntity mapSprint(SprintInfo sprint, int projectId);

    SprintDetails mapSprint(SprintEntity sprint);

    TaskDetails mapTask(TaskEntity entity);

    TaskEntity mapTask(TaskDetails entity);

    TaskEntity mapTaskFromInfo(TaskInfo entity, int projectId);

    AttachmentInfo mapAttachment(AttachmentEntity entity);
}
