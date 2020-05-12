package com.example.bachelor.impl;

import com.example.bachelor.api.ProjectInfo;
import com.example.bachelor.api.ProjectService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
@AllArgsConstructor
class ProjectServiceImpl implements ProjectService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectUserRepository projectUserRepository;
    private final ProjectMapper mapper;

    private ProjectUserEntity buildMember(String username, ProjectUserEntity.Permission permission, int projectId) {
        return ProjectUserEntity.builder().username(username).permission(permission).projectId(projectId).build();
    }

    @Override
    public int addProject(UserDetails owner, ProjectInfo project) {
        var newProject = ProjectEntity.builder().title(project.getTitle()).description(project.getDescription()).build();
        var savedProject = projectRepository.save(newProject);

        var members = userRepository.findAllByUsernameIn(project.getMembers()).stream()
                .filter(user -> !user.getUsername().equals(owner.getUsername()))
                .map(user -> buildMember(user.getUsername(), ProjectUserEntity.Permission.MEMBER, savedProject.getId()))
                .collect(toSet());

        members.add(buildMember(owner.getUsername(), ProjectUserEntity.Permission.OWNER, savedProject.getId()));
        projectUserRepository.saveAll(members);

        return savedProject.getId();
    }

    @Override
    public Collection<ProjectInfo> getProjects(UserDetails user) {
        var projectIds = projectUserRepository.findAllByUsername(user.getUsername())
                .map(ProjectUserEntity::getId).collect(toSet());

        return projectRepository.findAllByIdIn(projectIds).map(mapper::map).collect(toList());
    }
}
