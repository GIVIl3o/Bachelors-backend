package com.example.bachelor.rest;

import com.example.bachelor.api.AttachmentInfo;
import com.example.bachelor.api.ProjectService;
import com.example.bachelor.api.TaskDetails;
import com.example.bachelor.api.TaskInfo;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Log4j2
@RestController
@AllArgsConstructor
@Transactional
class TaskController {
    private final ProjectService projectService;

    @PostMapping("/tasks")
    @PreAuthorize("@projectServiceImpl.hasPermissionLevel(authentication.name, #projectId, 'DEVELOPER' ) and " +
            "@projectServiceImpl.hasPermissionLevel(#task.assignee, #projectId, 'DEVELOPER' )")
    public TaskDetails addTask(@RequestParam int projectId, @RequestBody TaskInfo task) {
        return projectService.addTask(projectId, task);
    }

    @PostMapping("/tasks/{taskId}/move")
    @PreAuthorize("@projectServiceImpl.hasPermissionLevel(authentication.name, #projectId, 'DEVELOPER' )")
    public void moveTask(@RequestParam int projectId, @PathVariable int taskId, @RequestParam(required = false) Integer sprintId,
                         @RequestParam(required = false) Integer previousLeft, @RequestParam(required = false) Integer previousRight,
                         @RequestParam(required = false) Integer nextLeft, @RequestParam(required = false) Integer nextRight,
                         @RequestParam TaskDetails.TaskProgress newProgress) {
        projectService.moveTask(taskId, sprintId, newProgress, previousLeft, previousRight, nextLeft, nextRight);
    }

    @PostMapping("/tasks/{taskId}")
    @PreAuthorize("@projectServiceImpl.hasPermissionLevel(authentication.name, #projectId, 'DEVELOPER' )")
    public void updateTask(@RequestParam int projectId, @RequestBody TaskDetails task) {
        projectService.updateTask(task);
    }

    @PostMapping("/tasks/{taskId}/attachment")
    @PreAuthorize("@projectServiceImpl.hasPermissionLevel(authentication.name, #projectId, 'DEVELOPER' )")
    public AttachmentInfo updateTask(@RequestParam int projectId, @PathVariable int taskId, @RequestParam MultipartFile attachment) throws IOException {
        return projectService.addAttachment(taskId, attachment.getOriginalFilename(),
                attachment.getContentType(), attachment.getSize(), attachment.getInputStream());
    }

    @GetMapping("/tasks/{taskId}/attachments")
    @PreAuthorize("@projectServiceImpl.hasPermissionLevel(authentication.name, #projectId, 'DEVELOPER' )")
    public List<AttachmentInfo> getAttachments(@RequestParam int projectId, @PathVariable int taskId) {
        return projectService.getAttachments(taskId);
    }

    @DeleteMapping("/tasks/{id}")
    @PreAuthorize("@projectServiceImpl.hasPermissionLevel(authentication.name, #projectId, 'DEVELOPER' )")
    public void deleteTask(@RequestParam int projectId, @PathVariable int id,
                           @RequestParam(required = false) Integer previousLeft,
                           @RequestParam(required = false) Integer previousRight) {

        projectService.deleteTask(id, previousLeft, previousRight);
    }
}
