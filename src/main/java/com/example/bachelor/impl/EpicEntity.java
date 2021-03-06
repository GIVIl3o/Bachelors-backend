package com.example.bachelor.impl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Data
@Builder
@Entity(name = "epics")
@NoArgsConstructor
@AllArgsConstructor
class EpicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NonNull
    private Integer projectId;

    @NonNull
    private String title;

    @NonNull
    private Date fromDate;

    @NonNull
    private Date toDate;
}
