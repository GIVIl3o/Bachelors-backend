package com.example.bachelor.api;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public interface ProjectService {

    int addProject(UserDetails user, ProjectInfo project);

    Collection<ProjectInfo> getProjects(UserDetails user);

    ProjectDetails getProject(int projectId);

    EpicInfo putEpic(int projectId, EpicInfo epic);

    void deleteEpic(int epicId);
}
