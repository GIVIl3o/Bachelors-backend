package com.example.bachelor.api;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.Set;

@Data
@Builder
public class ProjectDetails {

    @NonNull
    private final Integer id;

    @NonNull
    private final String title;

    @NonNull
    private final String description;

    @NonNull
    private final Set<ProjectUserInfo> members;

    @NonNull
    private final Set<EpicInfo> epics;
}
