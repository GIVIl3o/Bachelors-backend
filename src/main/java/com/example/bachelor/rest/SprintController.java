package com.example.bachelor.rest;

import com.example.bachelor.api.ProjectService;
import com.example.bachelor.api.SprintDetails;
import com.example.bachelor.api.SprintInfo;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Transactional
@RestController
@AllArgsConstructor
class SprintController {
    private final ProjectService service;

    @PutMapping("/sprints")
    public SprintDetails putSprint(@RequestParam int projectId, @RequestBody SprintInfo sprint) {
        return service.putSprint(projectId, sprint);
    }

    @DeleteMapping("/sprints/{sprintId}")
    public void deleteSprint(@RequestParam int projectId, @PathVariable int sprintId) {
        service.deleteSprint(sprintId);
    }
}
