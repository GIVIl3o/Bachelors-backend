package com.example.bachelor.impl;

import com.example.bachelor.api.AttachmentInfo;
import com.example.bachelor.api.CommentInfo;
import com.example.bachelor.api.EpicInfo;
import com.example.bachelor.api.NotificationInfo;
import com.example.bachelor.api.ProjectDetails;
import com.example.bachelor.api.ProjectInfo;
import com.example.bachelor.api.ProjectService;
import com.example.bachelor.api.ProjectUserInfo;
import com.example.bachelor.api.ProjectUserInfo.ProjectPermission;
import com.example.bachelor.api.SprintDetails;
import com.example.bachelor.api.SprintInfo;
import com.example.bachelor.api.TaskDetails;
import com.example.bachelor.api.TaskInfo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static java.util.stream.Collectors.toUnmodifiableSet;

@Service
@AllArgsConstructor
class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectUserRepository projectUserRepository;
    private final EpicRepository epicRepository;
    private final SprintRepository sprintRepository;
    private final TaskRepository taskRepository;
    private final AttachmentRepository attachmentRepository;
    private final ProjectMapper mapper;
    private final FileService fileService;
    private final WatchingRepository watchingRepository;
    private final CommentRepository commentRepository;
    private final NotificationRepository notificationRepository;

    private ProjectUserEntity buildMember(String username, ProjectPermission permission, int projectId) {
        return ProjectUserEntity.builder().username(username).permission(permission).projectId(projectId).build();
    }

    @Override
    public int addProject(UserDetails owner, ProjectInfo project) {
        var newProject = ProjectEntity.builder().title(project.getTitle()).description(project.getDescription()).build();
        var savedProject = projectRepository.save(newProject);

        var members = project.getMembers().stream()
                .map(member -> buildMember(member.getUsername(), member.getPermission(), savedProject.getId()))
                .collect(toSet());

        members.add(buildMember(owner.getUsername(), ProjectPermission.ADMIN, savedProject.getId()));
        projectUserRepository.saveAll(members);

        return savedProject.getId();
    }

    @Override
    public void updateProject(int projectId, ProjectInfo info) {
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("project " + projectId + " not found"));

        project.setTitle(info.getTitle());
        project.setDescription(info.getDescription());
        projectRepository.save(project);

        var members = project.getMembers();

        var memberPermission = info.getMembers().stream().collect(toUnmodifiableMap(ProjectUserInfo::getUsername, ProjectUserInfo::getPermission));

        var changedMembers = members.stream().filter(m -> !m.getPermission().equals(memberPermission.getOrDefault(m.getUsername(), m.getPermission())));
        var collectedMembers = changedMembers.collect(toUnmodifiableSet());
        collectedMembers.forEach(m -> m.setPermission(memberPermission.get(m.getUsername())));
        projectUserRepository.saveAll(collectedMembers);
    }

    @Override
    public void updateProjectPermission(int projectId, String username, ProjectUserInfo.ProjectPermission permission) {
        projectUserRepository.findByProjectIdAndUsername(projectId, username).ifPresent(entity -> {
            entity.setPermission(permission);
            projectUserRepository.save(entity);
        });
    }

    @Override
    public void inviteToProject(int projectId, String username, ProjectPermission permission) {
        var entity = ProjectUserEntity.builder().permission(permission)
                .projectId(projectId).username(username).build();

        projectUserRepository.save(entity);
    }

    @Override
    public Collection<ProjectInfo> getProjects(UserDetails user) {
        var projectIds = projectUserRepository.findAllByUsername(user.getUsername())
                .map(ProjectUserEntity::getProjectId).collect(toSet());

        return projectRepository.findAllByIdIn(projectIds).map(mapper::map).collect(toList());
    }

    @Override
    public ProjectDetails getProject(int projectId, String username) {
        var details = projectRepository.findById(projectId).map(mapper::mapToDetails)
                .orElseThrow(() -> new IllegalArgumentException("Project with id:" + projectId + " don't exists"));

        var taskWatching = watchingRepository.findByUsername(username).map(WatchingEntity::getTaskId).collect(toUnmodifiableSet());

        details.getTasks().forEach(task -> task.setWatching(taskWatching.contains(task.getId())));

        return details;
    }

    @Override
    public void changeOwner(int projectId, String fromName, String toName) {
        projectUserRepository.findByProjectIdAndUsername(projectId, fromName).ifPresent(t -> {
            t.setPermission(ProjectPermission.SCRUM_MASTER);
            projectUserRepository.save(t);
        });

        projectUserRepository.findByProjectIdAndUsername(projectId, toName).ifPresent(t -> {
            t.setPermission(ProjectPermission.ADMIN);
            projectUserRepository.save(t);
        });
    }

    @Override
    public void deleteProject(int projectId) {
        projectRepository.findById(projectId).ifPresent(project -> {
            taskRepository.deleteByProjectId(projectId);
            sprintRepository.deleteByProjectId(projectId);
            epicRepository.deleteByProjectId(projectId);

            projectUserRepository.deleteByProjectId(projectId);
            projectRepository.deleteById(projectId);
        });
    }

    @Override
    public void leaveProject(int projectId, String username) {
        var tasks = taskRepository.findByProjectIdAndAssignee(projectId, username).collect(Collectors.toUnmodifiableList());

        tasks.forEach(task -> task.setAssignee(null));

        tasks.forEach(taskRepository::save);

        projectUserRepository.deleteByProjectIdAndUsername(projectId, username);
    }

    public boolean hasPermissionLevel(String username, int projectId, ProjectPermission permission) {
        if (username == null)
            return true;

        return projectRepository.findById(projectId).stream()
                .flatMap(project -> project.getMembers().stream())
                .filter(member -> member.getUsername().equals(username))
                .anyMatch(member -> isHigherPermissionThan(member.getPermission(), permission));
    }

    private boolean isHigherPermissionThan(ProjectPermission given, ProjectPermission required) {
        if (required.equals(ProjectPermission.DEVELOPER))
            return true;
        if (required.equals(ProjectPermission.PRODUCT_OWNER) && !given.equals(ProjectPermission.DEVELOPER))
            return true;
        if (required.equals(ProjectPermission.SCRUM_MASTER) && !given.equals(ProjectPermission.DEVELOPER) && !given.equals(ProjectPermission.PRODUCT_OWNER)) {
            return true;
        }

        return given.equals(ProjectPermission.ADMIN);
    }

    @Override
    public EpicInfo putEpic(int projectId, EpicInfo epic) {
        var entity = mapper.mapEpic(epic, projectId);

        return mapper.mapEpic(epicRepository.save(entity));
    }

    @Override
    public void deleteEpic(int epicId) {
        sprintRepository.unconnectFromEpic(epicId);
        epicRepository.deleteById(epicId);
    }

    @Override
    public SprintDetails putSprint(int projectId, SprintInfo sprint) {
        var entity = mapper.mapSprint(sprint, projectId);

        return mapper.mapSprint(sprintRepository.save(entity));
    }

    @Override
    public void deleteSprint(int sprintId) {
        taskRepository.deleteBySprintIdAndProgress(sprintId, TaskDetails.TaskProgress.CLOSED);

        var tasks = taskRepository.findBySprintId(sprintId);
        tasks.forEach(t -> {
            t.setSprintId(null);
            t.setLeftId(null);
            t.setRightId(null);
        });
        taskRepository.saveAll(tasks);

        sprintRepository.deleteById(sprintId);
    }

    @Override
    public TaskDetails addTask(int projectId, TaskInfo task) {
        var taskEntity = mapper.mapTaskFromInfo(task, projectId);

        if (taskEntity.getSprintId() != null) {
            var rightTask = taskRepository.findBySprintIdAndProgressAndLeftIdIsNull(taskEntity.getSprintId(), taskEntity.getProgress());

            if (taskEntity.getRightId() == null && rightTask.isPresent())
                throw tasksWereUpdated();
            if (taskEntity.getRightId() != null && rightTask.isEmpty())
                throw tasksWereUpdated();

            if (taskEntity.getRightId() != null && rightTask.isPresent() && !taskEntity.getRightId().equals(rightTask.get().getId()))
                throw tasksWereUpdated();

            taskRepository.save(taskEntity);

            rightTask.ifPresent(t -> t.setLeftId(taskEntity.getId()));

            rightTask.ifPresent(taskRepository::save);

            // TODO remove
            //taskCheckNeighbors(taskEntity, null, rightTask.orElse(null));
        } else {
            taskRepository.save(taskEntity);
        }

        return mapper.mapTask(taskEntity);
    }

    @Override
    public AttachmentInfo addAttachment(int taskId, String filename, String contentType, long size, InputStream file) {
        taskRepository.findById(taskId).orElseThrow(this::tasksWereUpdated);

        var formatter = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss");
        var date = new Date();
        var dateString = formatter.format(date);

        var fullFileName = dateString + "-" + taskId + "-" + filename;

        var url = fileService.uploadAttachment(fullFileName, size, file);

        var entity = AttachmentEntity.builder().contentType(contentType).filename(filename).taskId(taskId).url(url).build();

        attachmentRepository.save(entity);

        return mapper.mapAttachment(entity);
    }

    @Override
    public void deleteAttachment(int id) {
        attachmentRepository.deleteById(id);
    }

    @Override
    public List<AttachmentInfo> getAttachments(int taskId) {
        taskRepository.findById(taskId).orElseThrow(this::tasksWereUpdated);

        return attachmentRepository.findByTaskId(taskId).map(mapper::mapAttachment).collect(toUnmodifiableList());
    }

    @Override
    public void updateTask(TaskDetails task, String username, boolean assigneeWatching) {
        taskRepository.findById(task.getId()).orElseThrow(this::tasksWereUpdated);
        taskRepository.save(mapper.mapTask(task));

        if (task.getAssignee() != null && assigneeWatching && !username.equals(task.getAssignee())) {
            watchingRepository.deleteByUsernameAndTaskId(task.getAssignee(), task.getId());
            watchingRepository.save(WatchingEntity.builder().taskId(task.getId()).username(task.getAssignee()).build());
        }

        watchingRepository.deleteByUsernameAndTaskId(username, task.getId());
        if (task.isWatching()) {
            watchingRepository.save(WatchingEntity.builder().taskId(task.getId()).username(username).build());
        }
    }

    @Override
    public void deleteTask(int taskId, Integer previousLeft, Integer previousRight) {
        var task = getTaskSafe(taskId);
        var previousLeftTask = getTaskSafe(previousLeft);
        var previousRightTask = getTaskSafe(previousRight);

        taskCheckNeighbors(task, previousLeftTask, previousRightTask);

        removeTaskFromOrder(previousLeftTask, previousRightTask);

        taskRepository.deleteById(taskId);
    }

    private void removeTaskFromOrder(@Nullable TaskEntity previousLeft, @Nullable TaskEntity previousRight) {
        if (previousLeft != null) {
            previousLeft.setRightId(previousRight == null ? null : previousRight.getId());
            taskRepository.save(previousLeft);
        }
        if (previousRight != null) {
            previousRight.setLeftId(previousLeft == null ? null : previousLeft.getId());
            taskRepository.save(previousRight);
        }
    }

    private void addTaskToOrder(TaskEntity task, Integer sprintId, TaskDetails.TaskProgress newProgress, @Nullable TaskEntity nextLeft, @Nullable TaskEntity nextRight) {
        if (nextLeft != null) {
            taskRepository.save(nextLeft.toBuilder().rightId(task.getId()).build());
        }
        if (nextRight != null) {
            taskRepository.save(nextRight.toBuilder().leftId(task.getId()).build());
        }

        var toSaveTask = task.toBuilder()
                .sprintId(sprintId)
                .progress(newProgress)
                .leftId(nextLeft == null ? null : nextLeft.getId())
                .rightId(nextRight == null ? null : nextRight.getId())
                .build();

        taskRepository.save(toSaveTask);

        // TODO remove
        //taskCheckNeighbors(toSaveTask, nextLeft, nextRight);
    }

    private ResponseStatusException tasksWereUpdated() {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Update tasks and try again");
    }

    TaskEntity getTaskSafe(@Nullable Integer taskId) {
        return taskId == null ? null : taskRepository.findById(taskId).orElseThrow(this::tasksWereUpdated);
    }

    private void taskCheckNeighbors(TaskEntity task, @Nullable TaskEntity left, @Nullable TaskEntity right) {
        if (left != null && !task.getProgress().equals(left.getProgress())) {
            throw tasksWereUpdated();
        }
        if (right != null && !task.getProgress().equals(right.getProgress())) {
            throw tasksWereUpdated();
        }

        if (task.getLeftId() == null) {
            if (left != null)
                throw tasksWereUpdated();
        } else {
            if (left == null || !task.getLeftId().equals(left.getId()) || !task.getProgress().equals(left.getProgress()))
                throw tasksWereUpdated();
        }

        if (task.getRightId() == null) {
            if (right != null)
                throw tasksWereUpdated();
        } else {
            if (right == null || !task.getRightId().equals(right.getId()) || !task.getProgress().equals(right.getProgress()))
                throw tasksWereUpdated();
        }
    }

    @Override
    public void moveTask(int taskId, Integer sprintId, TaskDetails.TaskProgress newProgress, Integer previousLeft, Integer previousRight, Integer nextLeft, Integer nextRight) {
        var task = getTaskSafe(taskId);
        var prevLeftTask = getTaskSafe(previousLeft);
        var prevRightTask = getTaskSafe(previousRight);
        var nextLeftTask = getTaskSafe(nextLeft);
        var nextRightTask = getTaskSafe(nextRight);

        taskCheckNeighbors(task, prevLeftTask, prevRightTask);

        removeTaskFromOrder(prevLeftTask, prevRightTask);
        addTaskToOrder(task, sprintId, newProgress, nextLeftTask, nextRightTask);
    }

    @Override
    public CommentInfo addComment(CommentInfo commentInfo) {
        var entity = mapper.mapComment(commentInfo);

        commentRepository.save(entity);

        return mapper.mapComment(entity);
    }

    @Override
    public List<CommentInfo> getComments(int taskId) {
        return commentRepository.findByTaskId(taskId).map(mapper::mapComment).collect(toUnmodifiableList());
    }

    @Override
    public List<NotificationInfo> addNotification(int taskId, String payload) {
        var watchers = watchingRepository.findByTaskId(taskId).map(WatchingEntity::getUsername).collect(toUnmodifiableSet());

        var notifications = watchers.stream()
                .map(username -> NotificationEntity.builder().username(username).payload(payload).build())
                .collect(toUnmodifiableList());

        notificationRepository.saveAll(notifications);
        notificationRepository.flush();

        return notifications.stream().map(mapper::mapNotification).collect(toUnmodifiableList());
    }

    @Override
    public void deleteNotification(int notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    @Override
    public List<NotificationInfo> getNotifications(String username) {
        return notificationRepository.findByUsername(username).stream().map(mapper::mapNotification)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Optional<SprintDetails> findSprint(int taskId) {
        return taskRepository.findById(taskId).map(TaskEntity::getSprintId).flatMap(sprintRepository::findById).map(mapper::mapSprint);
    }
}
