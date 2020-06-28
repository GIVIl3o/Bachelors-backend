package com.example.bachelor.api;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class AttachmentInfo {

    @NonNull
    private final Integer id;

    @NonNull
    private final String url;

    // original filename
    @NonNull
    private final String filename;

    @NonNull
    private final String contentType;
}
