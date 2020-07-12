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

@Data
@Builder
@Entity(name = "task_comments")
@NoArgsConstructor
@AllArgsConstructor
class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NonNull
    private String author;

    @NonNull
    private Integer taskId;

    @NonNull
    private Integer projectId;

    @NonNull
    private String text;
}
