package com.example.bachelor.api;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class SprintDetails {

    @NonNull
    private final Integer id;

    @NonNull
    private final String title;

    private final Integer epicId;

    private final boolean active;
}
