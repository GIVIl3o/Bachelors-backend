package com.example.bachelor.api;

import lombok.Data;
import lombok.NonNull;

@Data
public class TaskInfo {

    @NonNull
    private final String title;

    private final String assignee;

    private final Integer sprintId;

    @NonNull
    private final String description;

    @NonNull
    private final TaskDetails.TaskProgress progress;

    @NonNull
    private final TaskDetails.TaskType type;

    private final Integer leftId;
    private final Integer rightId;
}
