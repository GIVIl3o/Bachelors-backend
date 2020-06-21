package com.example.bachelor.rest;

import com.example.bachelor.api.ProjectService;
import com.example.bachelor.api.TaskDetails;
import com.example.bachelor.api.TaskInfo;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@AllArgsConstructor
@Transactional
class TaskController {
    private final ProjectService projectService;

    @PostMapping("/tasks")
    @PreAuthorize("@projectServiceImpl.hasPermissionLevel(authentication.name, #projectId, 'MEMBER' ) and " +
            "@projectServiceImpl.hasPermissionLevel(#task.assignee, #projectId, 'MEMBER' )")
    public TaskDetails addTask(@RequestParam int projectId, @RequestBody TaskInfo task) {
        return projectService.addTask(projectId, task);
    }

    @PostMapping("/tasks/{taskId}/reorder")
    @PreAuthorize("@projectServiceImpl.hasPermissionLevel(authentication.name, #projectId, 'MEMBER' )")
    public void moveTask(@RequestParam int projectId, @PathVariable int taskId, @RequestParam Integer sprintId,
                         @RequestParam Integer previousLeft, @RequestParam Integer previousRight,
                         @RequestParam Integer nextLeft, @RequestParam Integer nextRight) {
        projectService.moveTask(taskId, sprintId, previousLeft, previousRight, nextLeft, nextRight);
    }

    @PostMapping("/tasks/{taskId}")
    @PreAuthorize("@projectServiceImpl.hasPermissionLevel(authentication.name, #projectId, 'MEMBER' )")
    public void updateTask(@RequestParam int projectId, @RequestBody TaskDetails task) {
        projectService.updateTask(task);
    }

    @DeleteMapping("/tasks/{id}")
    @PreAuthorize("@projectServiceImpl.hasPermissionLevel(authentication.name, #projectId, 'MEMBER' )")
    public void deleteTask(@RequestParam int projectId, @PathVariable int id,
                           @RequestParam(required = false) Integer previousLeft,
                           @RequestParam(required = false) Integer previousRight) {

        projectService.deleteTask(id, previousLeft, previousRight);
    }
}
