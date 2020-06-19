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
import javax.persistence.OneToMany;
import java.util.Set;

@Data
@Builder
@Entity(name = "projects")
@NoArgsConstructor
@AllArgsConstructor
class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NonNull
    private String title;

    @NonNull
    private String description;

    @OneToMany(mappedBy = "projectId")
    private Set<ProjectUserEntity> members;

    @OneToMany(mappedBy = "projectId")
    private Set<EpicEntity> epics;

    @OneToMany(mappedBy = "projectId")
    private Set<TaskEntity> tasks;
}
