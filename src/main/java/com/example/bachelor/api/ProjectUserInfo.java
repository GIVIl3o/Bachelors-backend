package com.example.bachelor.api;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class ProjectUserInfo {

    public enum Permission {
        MEMBER,
        ADMIN,
        OWNER
    }

    @NonNull
    private final String username;

    @NonNull
    private final Permission permission;
}
