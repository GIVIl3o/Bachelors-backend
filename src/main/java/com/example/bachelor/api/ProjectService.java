package com.example.bachelor.api;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public interface ProjectService {

    int addProject(UserDetails user, ProjectInfo project);

    Collection<ProjectInfo> getProjects(UserDetails user);
}
