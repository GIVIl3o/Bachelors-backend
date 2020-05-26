package com.example.bachelor.api;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class EpicInfo {
    private final Integer id;

    @NonNull
    private final String title;


}
