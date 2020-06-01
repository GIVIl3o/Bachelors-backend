package com.example.bachelor.api;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class ProjectUserInfo {

    public enum ProjectPermission {
        MEMBER,
        ADMIN,
        OWNER
    }

    @NonNull
    private final String username;

    @NonNull
    private final ProjectPermission permission;
}
