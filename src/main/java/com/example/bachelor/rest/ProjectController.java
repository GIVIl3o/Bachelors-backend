package com.example.bachelor.rest;

import com.example.bachelor.api.ProjectDetails;
import com.example.bachelor.api.ProjectInfo;
import com.example.bachelor.api.ProjectService;
import com.example.bachelor.api.ProjectUserInfo;
import com.example.bachelor.api.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

import static java.util.stream.Collectors.toSet;

@Log4j2
@RestController
@AllArgsConstructor
@Transactional
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    @PutMapping("/projects")
    public int addProject(@AuthenticationPrincipal UserDetails user, @RequestBody ProjectInfo project) {
        var memberUsernames = project.getMembers().stream().map(ProjectUserInfo::getUsername).collect(toSet());
        if (!userService.existsByUsernamesAllIn(memberUsernames)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All members must exist before creating project");
        }

        return projectService.addProject(user, project);
    }

    @GetMapping("/projects")
    public Collection<ProjectInfo> getProjects(@AuthenticationPrincipal UserDetails user) {
        return projectService.getProjects(user);
    }

    @GetMapping("/projects/{projectId}")
    public ProjectDetails getProject(@PathVariable int projectId) {
        return projectService.getProject(projectId);
    }

    @PostMapping("/projects/{projectId}/change_owner/{newOwner}")
    @PreAuthorize("@projectServiceImpl.hasPermissionLevel(authentication.name, #projectId, 'OWNER' )")
    public void changeOwner(@AuthenticationPrincipal UserDetails user,
                            @PathVariable int projectId, @PathVariable String newOwner) {
        projectService.changeOwner(projectId, user.getUsername(), newOwner);
    }

    @DeleteMapping("/projects/{projectId}")
    @PreAuthorize("@projectServiceImpl.hasPermissionLevel(authentication.name, #projectId, 'OWNER' )")
    public void deleteProject(@PathVariable int projectId) {
        projectService.deleteProject(projectId);
    }

}
