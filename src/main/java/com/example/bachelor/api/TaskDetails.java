package com.example.bachelor.api;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class TaskDetails {
    public enum TaskProgress {
        PLANNED,
        TODO,
        DOING,
        REVIEW,
        QA,
        CLOSED
    }

    public enum TaskLabel {
        MINOR,
        MAJOR,
        BLOCKER,
        CRITICAL
    }

    public enum TaskType {
        STORY,
        BUG
    }

    @NonNull
    private final Integer id;

    @NonNull
    private Integer projectId;

    private Integer sprintId;

    private final String assignee;

    @NonNull
    private final String title;

    @NonNull
    private final String description;

    @NonNull
    private final TaskProgress progress;

    private final TaskLabel label;

    @NonNull
    private final TaskType type;

    private final Integer leftId;
    private final Integer rightId;
}
