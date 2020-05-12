package com.example.bachelor.impl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Data
@Builder
@Entity(name = "project_user")
@NoArgsConstructor
@AllArgsConstructor
class ProjectUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    //@ManyToOne
    //private ProjectEntity project;

    //@ManyToOne
    //private UserEntity user;

    private Integer projectId;

    @Column(name = "user_username")
    private String username;

    private Permission permission;

    public enum Permission {
        MEMBER,
        ADMIN,
        OWNER
    }

}
