package com.example.bachelor.impl;

import com.example.bachelor.api.EpicInfo;
import com.example.bachelor.api.ProjectDetails;
import com.example.bachelor.api.ProjectInfo;
import com.example.bachelor.api.ProjectService;
import com.example.bachelor.api.ProjectUserInfo.ProjectPermission;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
@AllArgsConstructor
class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectUserRepository projectUserRepository;
    private final EpicRepository epicRepository;
    private final ProjectMapper mapper;

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

        members.add(buildMember(owner.getUsername(), ProjectPermission.OWNER, savedProject.getId()));
        projectUserRepository.saveAll(members);

        return savedProject.getId();
    }

    @Override
    public Collection<ProjectInfo> getProjects(UserDetails user) {
        var projectIds = projectUserRepository.findAllByUsername(user.getUsername())
                .map(ProjectUserEntity::getProjectId).collect(toSet());

        return projectRepository.findAllByIdIn(projectIds).map(mapper::map).collect(toList());
    }

    @Override
    public ProjectDetails getProject(int projectId) {
        return projectRepository.findById(projectId).map(mapper::mapToDetals)
                .orElseThrow(() -> new IllegalArgumentException("Project with id:" + projectId + " don't exists"));
    }

    @Override
    public void deleteProject(int projectId) {
        projectRepository.findById(projectId).ifPresent(project -> {
            project.getEpics().stream().map(EpicEntity::getId).forEach(this::deleteEpic);
            projectUserRepository.deleteAllByProjectId(projectId);
            projectRepository.deleteById(projectId);
        });
    }

    public boolean hasPermissionLevel(String username, int projectId, ProjectPermission permission) {
        return projectRepository.findById(projectId).stream()
                .flatMap(project -> project.getMembers().stream())
                .filter(member -> member.getUsername().equals(username))
                .anyMatch(member -> isHigherPermissionThan(member.getPermission(), permission));
    }

    private boolean isHigherPermissionThan(ProjectPermission given, ProjectPermission required) {
        if (required.equals(ProjectPermission.MEMBER))
            return true;
        if (required.equals(ProjectPermission.ADMIN) && !given.equals(ProjectPermission.MEMBER))
            return true;

        return given.equals(ProjectPermission.OWNER);
    }

    @Override
    public EpicInfo putEpic(int projectId, EpicInfo epic) {
        var entity = mapper.mapEpic(epic, projectId);

        return mapper.mapEpic(epicRepository.save(entity));
    }

    @Override
    public void deleteEpic(int epicId) {
        epicRepository.deleteById(epicId);
    }

}
