package com.example.bachelor.api;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class CommentInfo {
    private final Integer id;

    private final int taskId;

    @NonNull
    private final Integer projectId;

    @NonNull
    private final String author;

    @NonNull
    private final String text;
}
