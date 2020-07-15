package com.example.bachelor.api;

import org.springframework.security.core.userdetails.UserDetails;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProjectService {

    int addProject(UserDetails user, ProjectInfo project);

    void updateProject(int projectId, ProjectInfo project);

    void updateProjectPermission(int projectId, String username, ProjectUserInfo.ProjectPermission permission);

    Collection<ProjectInfo> getProjects(UserDetails user);

    ProjectDetails getProject(int projectId, String username);

    void changeOwner(int projectId, String fromName, String toName);

    void deleteProject(int projectId);

    void leaveProject(int projectId, String username);

    void inviteToProject(int projectId, String username, ProjectUserInfo.ProjectPermission permission);

    EpicInfo putEpic(int projectId, EpicInfo epic);

    void deleteEpic(int epicId);

    SprintDetails putSprint(int projectId, SprintInfo sprint);

    void deleteSprint(int sprintId);

    TaskDetails addTask(int projectId, TaskInfo task);

    AttachmentInfo addAttachment(int taskId, String filename, String contentType, long size, InputStream file);

    void deleteAttachment(int id);

    List<AttachmentInfo> getAttachments(int taskId);

    void updateTask(TaskDetails task, String username, boolean assigneeWatching);

    void moveTask(int taskId, Integer sprintId, TaskDetails.TaskProgress newProgress, Integer previousLeft, Integer previousRight, Integer nextLeft, Integer nextRight);

    void deleteTask(int taskId, Integer previousLeft, Integer previousRight);

    CommentInfo addComment(CommentInfo commentInfo);

    List<CommentInfo> getComments(int taskId);

    List<NotificationInfo> addNotification(int taskId, String payload);

    void deleteNotification(int notificationId);

    List<NotificationInfo> getNotifications(String username);

    Optional<SprintDetails> findSprint(int taskId);
}
