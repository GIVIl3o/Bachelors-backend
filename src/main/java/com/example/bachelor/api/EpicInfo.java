package com.example.bachelor.api;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.Date;

@Data
@Builder(toBuilder = true)
public class EpicInfo {

    private final Integer id;

    @NonNull
    private final String title;

    @NonNull
    private final Date fromDate;

    @NonNull
    private final Date toDate;
}
