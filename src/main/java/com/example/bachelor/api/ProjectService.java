package com.example.bachelor.api;

import org.springframework.security.core.userdetails.UserDetails;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public interface ProjectService {

    int addProject(UserDetails user, ProjectInfo project);

    Collection<ProjectInfo> getProjects(UserDetails user);

    ProjectDetails getProject(int projectId);

    void changeOwner(int projectId, String fromName, String toName);

    void deleteProject(int projectId);

    EpicInfo putEpic(int projectId, EpicInfo epic);

    void deleteEpic(int epicId);

    SprintDetails putSprint(int projectId, SprintInfo sprint);

    void deleteSprint(int sprintId);

    TaskDetails addTask(int projectId, TaskInfo task);

    AttachmentInfo addAttachment(int taskId, String filename, String contentType, long size, InputStream file);

    List<AttachmentInfo> getAttachments(int taskId);

    void updateTask(TaskDetails task);

    void moveTask(int taskId, Integer sprintId, TaskDetails.TaskProgress newProgress, Integer previousLeft, Integer previousRight, Integer nextLeft, Integer nextRight);

    void deleteTask(int taskId, Integer previousLeft, Integer previousRight);
}
