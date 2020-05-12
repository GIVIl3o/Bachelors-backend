package com.example.bachelor.rest;


import com.example.bachelor.api.ProjectInfo;
import com.example.bachelor.api.ProjectService;
import com.example.bachelor.api.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@Log4j2
@RestController
@RequestMapping("projects")
@AllArgsConstructor
@Transactional
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public int addProject(@AuthenticationPrincipal UserDetails user, @RequestBody ProjectInfo project) {
        if (!userService.existsByUsernamesAllIn(project.getMembers())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All members must exist before creating project");
        }

        return projectService.addProject(user, project);
    }

    @GetMapping
    public Collection<ProjectInfo> getProjects(@AuthenticationPrincipal UserDetails user) {
        return projectService.getProjects(user);
    }

}
