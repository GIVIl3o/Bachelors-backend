package com.example.bachelor.api;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class ProjectUserInfo {

    public enum ProjectPermission {
        DEVELOPER,
        PRODUCT_OWNER,
        SCRUM_MASTER,
        ADMIN
    }

    @NonNull
    private final String username;

    @NonNull
    private final ProjectPermission permission;
}
