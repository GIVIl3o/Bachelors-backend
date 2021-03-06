package com.example.bachelor.impl;

import com.example.bachelor.api.ProjectUserInfo.ProjectPermission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Builder
@Entity(name = "project_user")
@NoArgsConstructor
@AllArgsConstructor
class ProjectUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NonNull
    private Integer projectId;

    @NonNull
    @Column(name = "user_username")
    private String username;

    @NonNull
    private ProjectPermission permission;


}
