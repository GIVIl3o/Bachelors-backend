package com.example.bachelor.api;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class NotificationInfo {

    @NonNull
    private final Integer id;

    @NonNull
    private final String username;

    private final Integer projectId;

    @NonNull
    private final String payload;
}
