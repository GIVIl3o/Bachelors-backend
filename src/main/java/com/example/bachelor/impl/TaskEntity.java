package com.example.bachelor.impl;

import com.example.bachelor.api.TaskDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Builder(toBuilder = true)
@Entity(name = "tasks")
@NoArgsConstructor
@AllArgsConstructor
class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NonNull
    private Integer projectId;

    private Integer sprintId;

    private String assignee;

    @NonNull
    private String title;

    @NonNull
    private String description;

    @NonNull
    private TaskDetails.TaskProgress progress;

    private TaskDetails.TaskLabel label;

    private Integer leftId;
    private Integer rightId;
}
