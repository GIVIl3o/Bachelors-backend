package com.example.bachelor.rest;

import com.example.bachelor.api.EpicInfo;
import com.example.bachelor.api.ProjectService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@AllArgsConstructor
@Transactional
class EpicController {

    private final ProjectService projectService;

    @PutMapping("/epics")
    @PreAuthorize("@projectServiceImpl.hasPermissionLevel(authentication.name, #projectId, 'ADMIN' )")
    public EpicInfo putEpic(@RequestParam int projectId, @RequestBody EpicInfo epic) {
        return projectService.putEpic(projectId, epic);
    }

    @DeleteMapping("/epics/{epicId}")
    @PreAuthorize("@projectServiceImpl.hasPermissionLevel(authentication.name, #projectId, 'OWNER' )")
    public void deleteEpic(@RequestParam int projectId, @PathVariable int epicId) {
        projectService.deleteEpic(epicId);
    }
}
