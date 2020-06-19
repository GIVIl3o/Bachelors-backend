package com.example.bachelor.api;

import lombok.Data;
import lombok.NonNull;

@Data
public class TaskInfo {

    @NonNull private final String title;

    private final String assignee;

    @NonNull private final String description;
}