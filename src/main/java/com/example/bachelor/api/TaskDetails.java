package com.example.bachelor.api;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class TaskDetails {

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
}
